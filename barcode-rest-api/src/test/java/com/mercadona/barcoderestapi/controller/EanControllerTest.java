package com.mercadona.barcoderestapi.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.mercadona.barcoderestapi.BarcodeRestApiApplication;
import com.mercadona.barcoderestapi.model.Product;
import com.mercadona.barcoderestapi.model.Provider;
import com.mercadona.barcoderestapi.service.EanService;
import com.mercadona.barcoderestapi.service.ProductService;
import com.mercadona.barcoderestapi.service.ProviderService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BarcodeRestApiApplication.class)
@AutoConfigureMockMvc
public class EanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CacheManager cacheManager;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProviderService providerService;

    @MockBean
    private EanService eanService;

    public void evictAllCaches() {
        cacheManager.getCacheNames().stream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }
    //before each test, clear the cache since the controller we are testing is using it.
    @BeforeEach
    public void setUp() {
        evictAllCaches();
    }

    @Test
    public void testGetEan13() {

        String productName = "Test Product";
        String productDescription = "Test Product Description";
        String productBarcode = "55555";
        BigDecimal productPrice = new BigDecimal(1.99);

        Product mockProduct = new Product(productBarcode, productName, productPrice, productDescription);

        String barcode = "7777777";
        String providerName = "Test Provider";
        String providerAddress = "Test Provider Address";
        Provider mockProvider = new Provider(barcode, providerName, providerAddress);

        String ean13 = "7777777555552";

        when(productService.getById(productBarcode)).thenReturn(Optional.of(mockProduct));
        when(providerService.getById(barcode)).thenReturn(Optional.of(mockProvider));
        when(eanService.getEan13(ean13)).thenReturn(new HashMap<>() {
            {
                put("providerBarcode", barcode);
                put("productBarcode", productBarcode);
                put("detination", "2");
            }
        });
        when(eanService.eanValidator(ean13)).thenReturn("success");
        when(eanService.validateProductAndProvideCodes(barcode, productBarcode)).thenReturn("success");

        assertDoesNotThrow(() -> {
            MvcResult result = mockMvc.perform(get("/getEan/" + ean13)).andExpect(status().isOk()).andReturn();
            String content = result.getResponse().getContentAsString();
            assertTrue(content.contains(productName));
            assertTrue(content.contains(productDescription));
            assertTrue(content.contains(productBarcode));
            assertTrue(content.contains(productPrice.toString()));
            assertTrue(content.contains(providerName));
            assertTrue(content.contains(providerAddress));
            assertTrue(content.contains(barcode));
            assertTrue(content.contains("2"));
        });

    }

    @Test
    public void testGetEan13InvalidEan() {

        String ean13 = "7777777555552";

        when(eanService.eanValidator(ean13)).thenReturn("invalid ean");

        assertDoesNotThrow(() -> {
            MvcResult result = mockMvc.perform(get("/getEan/" + ean13)).andExpect(status().isBadRequest()).andReturn();
            String content = result.getResponse().getContentAsString();
            assertTrue(content.contains("invalid ean"));
        });

    }

    @Test
    public void testGetEan13InvalidProductCode() {

        String ean13 = "77777775555S2";

        when(eanService.eanValidator(ean13)).thenReturn("success");
        when(eanService.getEan13(ean13)).thenReturn(new HashMap<>() {
            {
                put("providerBarcode", "7777777");
                put("productBarcode", "5555S");
                put("detination", "2");
            }
        });
        when(eanService.validateProductAndProvideCodes("7777777", "5555S")).thenReturn("invalid product code");

        assertDoesNotThrow(() -> {
            MvcResult result = mockMvc.perform(get("/getEan/" + ean13)).andExpect(status().isBadRequest()).andReturn();
            String content = result.getResponse().getContentAsString();
            assertTrue(content.contains("invalid product code"));
        });

    }

    @Test
    public void testGetEan13InvalidProviderCode() {

        String ean13 = "777777S5555S2";

        when(eanService.eanValidator(ean13)).thenReturn("success");
        when(eanService.getEan13(ean13)).thenReturn(new HashMap<>() {
            {
                put("providerBarcode", "777777S");
                put("productBarcode", "55555");
                put("detination", "2");
            }
        });
        when(eanService.validateProductAndProvideCodes("777777S", "55555")).thenReturn("invalid provider code");

        assertDoesNotThrow(() -> {
            MvcResult result = mockMvc.perform(get("/getEan/" + ean13)).andExpect(status().isBadRequest()).andReturn();
            String content = result.getResponse().getContentAsString();
            assertTrue(content.contains("invalid provider code"));
        });
    }

    @Test
    public void testGetEan13NotExistingProducts() throws Exception {

        String productName = "Test Product";
        String productDescription = "Test Product Description";
        String productBarcode = "55555";
        BigDecimal productPrice = new BigDecimal(1.99);

        Product mockProduct = new Product(productBarcode, productName, productPrice, productDescription);

        String barcode = "7777777";

        String ean13 = "7777777555552";

        when(productService.getById(productBarcode)).thenReturn(Optional.of(mockProduct));
        when(providerService.getById(barcode)).thenReturn(Optional.empty());
        when(eanService.getEan13(ean13)).thenReturn(new HashMap<>() {
            {
                put("providerBarcode", barcode);
                put("productBarcode", productBarcode);
                put("detination", "2");
            }
        });
        when(eanService.eanValidator(ean13)).thenReturn("success");
        when(eanService.validateProductAndProvideCodes(barcode, productBarcode)).thenReturn("success");

        MvcResult result = assertDoesNotThrow(
                () -> mockMvc.perform(get("/getEan/" + ean13)).andExpect(status().isBadRequest()).andReturn());

        assertTrue(result.getResponse().getContentAsString().contains("Could not resolve the EAN"));
    }
}
