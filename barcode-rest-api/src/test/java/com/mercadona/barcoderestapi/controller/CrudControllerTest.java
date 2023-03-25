package com.mercadona.barcoderestapi.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.*;

import com.mercadona.barcoderestapi.BarcodeRestApiApplication;
import com.mercadona.barcoderestapi.model.Product;
import com.mercadona.barcoderestapi.service.ProductService;
import com.mercadona.barcoderestapi.service.ProviderService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BarcodeRestApiApplication.class)
@AutoConfigureMockMvc
public class CrudControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProviderService providerService;

    @Test
    public void testGetProduct() throws Exception {
        String productName = "Test Product";
        String productDescription = "Test Product Description";
        Integer productBarcode = 55555;
        BigDecimal productPrice = new BigDecimal(1.99);

        Product mockProduct = new Product(productBarcode, productName, productPrice, productDescription);

        when(productService.getById(productBarcode)).thenReturn(Optional.of(mockProduct));
        // assert that the product is returned correctly
        assertDoesNotThrow(
                () -> mockMvc.perform(get("/getProduct/{barcode}", productBarcode)).andExpect(status().isOk()));
        // assert that the productService.getById method is called once
        verify(productService, only()).getById(55555);

    }

    @Test
    public void testGetNonexistantProduct() throws Exception {
        String productBarcode = "55555";

        when(productService.getById(Integer.parseInt(productBarcode))).thenReturn(Optional.empty());

        MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/getProduct/{barcode}", productBarcode))
                .andExpect(status().isBadRequest()).andReturn());

        verify(productService, only()).getById(55555);

        // assert that the correct error message is returned, may not be a good idea to
        // check since this message is subject to change
        assertEquals("{\"message\":\"Product not found\"}", result.getResponse().getContentAsString());

    }

    @Test
    public void testGetProductInvalidBarcode() throws Exception {
        String productBarcode = "555SA";

        MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/getProduct/{barcode}", productBarcode))
                .andExpect(status().isBadRequest()).andReturn());

        // assert that the correct error message is returned, may not be a good idea to
        // check since this message is subject to change
        assertEquals("{\"message\":\"Barcode must be a int number\"}", result.getResponse().getContentAsString());

    }

    @Test
    public void testGetProductInvalidBarcode2() throws Exception {
        String productBarcode = "555.5";

        MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/getProduct/{barcode}", productBarcode))
                .andExpect(status().isBadRequest()).andReturn());

        // assert that the correct error message is returned, may not be a good idea to
        // check since this message is subject to change
        assertEquals("{\"message\":\"Barcode must be a int number\"}", result.getResponse().getContentAsString());

    }

    @Test
    public void testCreateProduct() throws Exception {
        String productName = "Test Product";
        String productDescription = "Test Product Description";
        Integer productBarcode = 55555;
        BigDecimal productPrice = BigDecimal.valueOf(1.99);

        Product mockProduct = new Product(productBarcode, productName, productPrice, productDescription);

        when(productService.saveOrUpdate(mockProduct)).thenReturn(mockProduct);

        MvcResult result = assertDoesNotThrow(() -> mockMvc
                .perform(post("/addProduct").contentType("application/json").content(
                        "{\"barcode\":55555,\"name\":\"Test Product\",\"price\":1.99,\"description\":\"Test Product Description\"}"))
                .andExpect(status().isCreated()).andReturn());

        
        // assert that the correct product is returned
        assertTrue(result.getResponse().getContentAsString().contains("\"barcode\":55555"));

    }

    @Test
    public void testCreateProductInvalidBarcode() throws Exception {
        MvcResult result = assertDoesNotThrow(() -> mockMvc
                .perform(post("/addProduct").contentType("application/json").content(
                        "{\"barcode\":\"SSSS\",\"name\":\"Test Product\",\"price\":1.99,\"description\":\"Test Product Description\"}"))
                .andExpect(status().isBadRequest()).andReturn());


        // assert that error message is returned
        assertTrue(result.getResponse().getContentAsString().contains("error"));
    }

}
