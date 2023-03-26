package com.mercadona.barcoderestapi.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.*;

import com.mercadona.barcoderestapi.BarcodeRestApiApplication;
import com.mercadona.barcoderestapi.model.Product;
import com.mercadona.barcoderestapi.model.Provider;
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

        @Autowired
        CacheManager cacheManager;

        @MockBean
        private ProviderService providerService;

        public void evictAllCaches() {
                cacheManager.getCacheNames().stream()
                                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
        }
        //Before each test, clear the cache to avoid problems
        @BeforeEach
        public void setUp() {
                evictAllCaches();
        }

        @Test
        public void testGetProduct() throws Exception {
                String productName = "Test Product";
                String productDescription = "Test Product Description";
                String productBarcode = "55555";
                BigDecimal productPrice = new BigDecimal(1.99);

                Product mockProduct = new Product(productBarcode, productName, productPrice, productDescription);

                when(productService.getById(productBarcode)).thenReturn(Optional.of(mockProduct));
                when(productService.validBarcode(productBarcode)).thenReturn("success");
                // assert that the product is returned correctly
                assertDoesNotThrow(
                                () -> mockMvc.perform(get("/getProduct/{barcode}", productBarcode))
                                                .andExpect(status().isOk()).andReturn());
                // assert that the productService is called as we expect

                verify(productService, times(1)).getById(productBarcode);
                verify(productService, times(1)).validBarcode(productBarcode);

        }

        @Test
        public void testGetNonexistantProduct() throws Exception {
                String productBarcode = "55555";

                when(productService.getById(productBarcode)).thenReturn(Optional.empty());
                when(productService.validBarcode(productBarcode)).thenReturn("success");

                MvcResult result = assertDoesNotThrow(
                                () -> mockMvc.perform(get("/getProduct/{barcode}", productBarcode))
                                                .andExpect(status().isBadRequest()).andReturn());

                verify(productService, times(1)).getById(productBarcode);
                verify(productService, times(1)).validBarcode(productBarcode);

                // assert that the correct error message is returned, may not be a good idea to
                // check since this message is subject to change
                assertEquals("{\"message\":\"Product not found\"}", result.getResponse().getContentAsString());

        }

        @Test
        public void testGetProductInvalidBarcode() throws Exception {
                String productBarcode = "555SA";
                when(productService.validBarcode(productBarcode)).thenReturn("Barcode Must be a int number");
                MvcResult result = assertDoesNotThrow(
                                () -> mockMvc.perform(get("/getProduct/{barcode}", productBarcode))
                                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the correct error message is returned, may not be a good idea

                // check since this message is subject to change
                assertTrue(
                                result.getResponse().getContentAsString().contains("Barcode Must be a int number"));

        }

        @Test
        public void testGetProductInvalidBarcode2() throws Exception {
                String productBarcode = "555.5";

                when(productService.validBarcode(productBarcode)).thenReturn("Barcode Must be a int number");

                MvcResult result = assertDoesNotThrow(
                                () -> mockMvc.perform(get("/getProduct/{barcode}", productBarcode))
                                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the correct error message is returned, may not be a good idea to
                // check since this message is subject to change
                assertTrue(
                                result.getResponse().getContentAsString().contains("Barcode Must be a int number"));

        }

        @Test
        public void testCreateProduct() throws Exception {
                String productName = "Test Product";
                String productDescription = "Test Product Description";
                String productBarcode = "55555";
                BigDecimal productPrice = BigDecimal.valueOf(1.99);

                Product mockProduct = new Product(productBarcode, productName, productPrice,
                                productDescription);

                when(productService.saveOrUpdate(mockProduct)).thenReturn(mockProduct);
                when(productService.validBarcode(productBarcode)).thenReturn("success");
                when(productService.existsById(productBarcode)).thenReturn(false);

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProduct").contentType("application/json").content(
                                                "{\"barcode\":55555,\"name\":\"Test Product\",\"price\":1.99,\"description\":\"Test Product Description\"}"))
                                .andExpect(status().isCreated()).andReturn());

                // assert that the correct product is returned

                assertTrue(result.getResponse().getContentAsString().contains("\"barcode\":\"55555\""));

        }

        @Test
        public void testCreateAlreadyExistingProduct() throws Exception {
                String productName = "Test Product";
                String productDescription = "Test Product Description";
                String productBarcode = "55555";
                BigDecimal productPrice = BigDecimal.valueOf(1.99);

                Product mockProduct = new Product(productBarcode, productName, productPrice,
                                productDescription);

                when(productService.saveOrUpdate(mockProduct)).thenReturn(mockProduct);
                when(productService.validBarcode(productBarcode)).thenReturn("success");
                when(productService.existsById(productBarcode)).thenReturn(true);

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProduct").contentType("application/json").content(
                                                "{\"barcode\":55555,\"name\":\"Test Product\",\"price\":1.99,\"description\":\"Test Product Description\"}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the correct product is returned

                assertTrue(result.getResponse().getContentAsString()
                                .contains("Provider with the barcode 55555 already exists"));
        }

        @Test
        public void testCreateProductInvalidBarcode() throws Exception {
                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProduct").contentType("application/json").content(
                                                "{\"barcode\":\"SSSSS\",\"name\":\"Test Product\",\"price\":1.99,\"description\":\"Test Product Description\"}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that error message is returned
                assertTrue(result.getResponse().getContentAsString().contains("The barcode must be a number"));
        }

        @Test
        public void testCreateProductInvalidPrice() throws Exception {
                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProduct").contentType("application/json").content(
                                                "{\"barcode\":55555,\"name\":\"TestProduct\",\"price\":\"SSSS\",\"description\":\"Test Product Description\"}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that error message is returned
                assertTrue(result.getResponse().getContentAsString().contains("error"));
        }

        @Test
        public void testCreateProductInvalidDescription() throws Exception {
                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProduct").contentType("application/json").content(
                                                "{\"barcode\":55555,\"name\":\"TestProduct\",\"price\":1.99,\"description\":34}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that error message is returned
                assertTrue(result.getResponse().getContentAsString()
                                .contains("The description must be between 4 and 300 characters"));
        }

        @Test
        public void testUpdateProduct() throws Exception {
                String productName = "Test Product";
                String productDescription = "Test Product Description";
                String productBarcode = "55555";
                BigDecimal productPrice = BigDecimal.valueOf(1.99);

                Product mockProduct = new Product(productBarcode, productName, productPrice,
                                productDescription);

                when(productService.saveOrUpdate(mockProduct)).thenReturn(mockProduct);
                when(productService.existsById(productBarcode)).thenReturn(true);
                when(productService.validBarcode(productBarcode)).thenReturn("success");

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(put("/updateProduct/55555").contentType("application/json").content(
                                                "{\"barcode\":55555,\"name\":\"Test Product\",\"price\":1.99,\"description\":\"Test Product Description\"}"))
                                .andExpect(status().isOk()).andReturn());

                // assert that the correct product is returned
                assertTrue(result.getResponse().getContentAsString().contains("\"barcode\":\"55555\""));
                verify(productService, times(1)).existsById(productBarcode);

        }

        // When updating a product, if the barcode is changed to another barcode that
        // alredy exists, the product should not be updated
        @Test
        public void testUpdateProductUpdatingBarcode() throws Exception {
                String productName = "Test Product";
                String productDescription = "Test Product Description";
                String productBarcode = "44444";
                BigDecimal productPrice = BigDecimal.valueOf(1.99);
                String formerProductBarcode = "55555";

                Product mockProduct = new Product(productBarcode, productName, productPrice,
                                productDescription);

                when(productService.saveOrUpdate(mockProduct)).thenReturn(mockProduct);
                when(productService.existsById(formerProductBarcode)).thenReturn(true);
                when(productService.validBarcode(formerProductBarcode)).thenReturn("success");
                doNothing().when(productService).delete(formerProductBarcode);

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(put("/updateProduct/55555").contentType("application/json").content(
                                                "{\"barcode\":\"44444\",\"name\":\"Test Product\",\"price\":1.99,\"description\":\"Test Product Description\"}"))
                                .andExpect(status().isOk()).andReturn());

                // assert that the correct product is returned
                assertTrue(result.getResponse().getContentAsString().contains("\"barcode\":\"44444\""));
                verify(productService, times(1)).existsById(formerProductBarcode);
                verify(productService, times(1)).delete(formerProductBarcode);
        }

        // The Entity validation for editing/updating is the same as for creating,
        // so I dont find it completely necessary to test all the different cases for
        // editing/updating
        @Test
        public void testUpdateNotExistingProduct() throws Exception {

        when(productService.existsById("55555")).thenReturn(false);
        when(productService.validBarcode("55555")).thenReturn("success");

        MvcResult result = assertDoesNotThrow(() -> mockMvc
        .perform(put("/updateProduct/55555").contentType("application/json").content(
        "{\"barcode\":55555,\"name\":\"Test Product\",\"price\":1.99,\"description\":\"Test Product Description\"}"))
        .andExpect(status().isNotFound()).andReturn());

        // assert that error message is returned
        assertTrue(result.getResponse().getContentAsString().contains("Product not found"));
        }

        @Test
        public void testDeleteProduct() throws Exception {
                String productBarcode = "55555";

                when(productService.existsById(productBarcode)).thenReturn(true);
                when(productService.validBarcode("55555")).thenReturn("success");
                doNothing().when(productService).delete(productBarcode);

                MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(delete("/deleteProduct/" + productBarcode))
                                .andExpect(status().isOk()).andReturn());

                // assert that the correct product is returned
                assertTrue(result.getResponse().getContentAsString().contains("Product deleted successfully"));
                verify(productService, times(1)).existsById(productBarcode);
                verify(productService, times(1)).delete(productBarcode);
        }

        @Test
        public void testDeleteNotExistingProduct() throws Exception {
                String productBarcode = "55555";

                when(productService.existsById(productBarcode)).thenReturn(false);
                when(productService.validBarcode("55555")).thenReturn("success");

                MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(delete("/deleteProduct/" + productBarcode))
                                .andExpect(status().isNotFound()).andReturn());

                // assert that the correct product is returned
                assertTrue(result.getResponse().getContentAsString().contains("Product not found"));
                verify(productService, times(1)).existsById(productBarcode);

        }

        @Test
        public void testGetProvider() throws Exception {
                String barcode = "7777777";
                String providerName = "Test Provider";
                String providerAddress = "Test Provider Address";
                Provider mockProvider = new Provider(barcode, providerName, providerAddress);

                when(providerService.getById(barcode)).thenReturn(Optional.of(mockProvider));
                when(providerService.validBarcode("7777777")).thenReturn("success");

                MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/getProvider/" + barcode))
                                .andExpect(status().isOk()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString().contains("\"barcode\":\"7777777\""));
        }

        @Test
        public void testGetNotExistingProvider() throws Exception {
                String barcode = "7777777";

                when(providerService.getById(barcode)).thenReturn(Optional.empty());
                when(providerService.validBarcode("7777777")).thenReturn("success");

                MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/getProvider/" + barcode))
                                .andExpect(status().isNotFound()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString().contains("Provider not found"));
        }

        @Test
        public void testGetProviderInvalidBarcode() throws Exception {
                String barcode = "invalid Barcode";

                when(providerService.validBarcode(barcode)).thenReturn("Barcode must be a int number");

                MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/getProvider/" + barcode))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString().contains("Barcode must be a int number"));
        }

        @Test
        public void testCreateProvider() throws Exception {
                String providerName = "Test Provider";
                String providerAddress = "Test Provider Address";
                String providerBarcode = "7777777";

                Provider mockProvider = new Provider(providerBarcode, providerName,
                                providerAddress);

                when(providerService.saveOrUpdate(mockProvider)).thenReturn(mockProvider);
                when(providerService.existsById(providerBarcode)).thenReturn(false);

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProvider").contentType("application/json").content(
                                                "{\"barcode\":7777777,\"name\":\"Test Provider\",\"address\":\"Test Provider Address\"}"))
                                .andExpect(status().isCreated()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString().contains("\"barcode\":\"7777777\""));

        }

        @Test
        public void testCreateProviderInvalidBarcode() throws Exception {

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProvider").contentType("application/json").content(
                                                "{\"barcode\":\"invalid Barcode\",\"name\":\"Test Provider\",\"address\":\"Test Provider Address\"}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString().contains("The barcode must be a number"));

        }

        @Test
        public void testCreateProviderInvalidName() throws Exception {

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProvider").contentType("application/json").content(
                                                "{\"barcode\":7777777,\"name\":\"\",\"address\":\"Test Provider Address\"}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString()
                                .contains("The name must be between 2 and 30 characters"));
        }

        @Test
        public void testCreateAlreadyExistingProvider() throws Exception {

                String providerName = "Test Provider";
                String providerAddress = "Test Provider Address";
                String providerBarcode = "7777777";

                Provider mockProvider = new Provider(providerBarcode, providerName,
                                providerAddress);

                when(providerService.saveOrUpdate(mockProvider)).thenReturn(mockProvider);
                when(providerService.existsById(providerBarcode)).thenReturn(true);

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProvider").contentType("application/json").content(
                                                "{\"barcode\":7777777,\"name\":\"Test Provider\",\"address\":\"Test Provider Address\"}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the error message is returned
                assertTrue(result.getResponse().getContentAsString()
                                .contains("Provider with the barcode 7777777 already exists"));
        }

        @Test
        public void testCreateProviderInvalidAddress() throws Exception {

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProvider").contentType("application/json").content(

                                                "{\"barcode\":7777777,\"name\":\"Test Provider\",\"address\":\"\"}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString()
                                .contains("The adress must be between 4 and 300 characters"));

        }

        @Test
        public void testCreateProviderInvalidNot7DigitsBarcode() throws Exception {

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(post("/addProvider").contentType("application/json").content(
                                                "{\"barcode\":777777,\"name\":\"Test Provider\",\"address\":\"Test Provider Address\"}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString()
                                .contains("The barcode must be 7 numbers"));

        }

        @Test
        public void testUpdateProvider() throws Exception {
                String providerName = "Test Provider";
                String providerAddress = "Test Provider Address";
                String providerBarcode = "7777777";

                Provider mockProvider = new Provider(providerBarcode, providerName,
                                providerAddress);

                when(providerService.getById(providerBarcode)).thenReturn(Optional.of(mockProvider));
                when(providerService.saveOrUpdate(mockProvider)).thenReturn(mockProvider);
                when(providerService.existsById(providerBarcode)).thenReturn(true);
                when(providerService.validBarcode("7777777")).thenReturn("success");

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(put("/updateProvider/7777777").contentType("application/json").content(
                                                "{\"barcode\":7777777,\"name\":\"Test Provider\",\"address\":\"Test Provider Address\"}"))
                                .andExpect(status().isCreated()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString().contains("\"barcode\":\"7777777\""));
        }

        @Test
        public void testUpdateNotExistingProvider() throws Exception {
                String providerName = "Test Provider";
                String providerAddress = "Test Provider Address";
                String providerBarcode = "7777777";

                Provider mockProvider = new Provider(providerBarcode, providerName,
                                providerAddress);

                when(providerService.getById(providerBarcode)).thenReturn(Optional.empty());
                when(providerService.saveOrUpdate(mockProvider)).thenReturn(mockProvider);
                // The provider does not exist
                when(providerService.existsById(providerBarcode)).thenReturn(false);
                when(providerService.validBarcode("7777777")).thenReturn("success");

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(put("/updateProvider/7777777").contentType("application/json").content(
                                                "{\"barcode\":7777777,\"name\":\"Test Provider\",\"address\":\"Test Provider Address\"}"))
                                .andExpect(status().isNotFound()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString().contains("Provider not found"));
        }

        @Test
        public void testUpdateProviderInvalidBarcode() throws Exception {
                String providerName = "Test Provider";
                String providerAddress = "Test Provider Address";
                String providerBarcode = "invalid Barcode";

                Provider mockProvider = new Provider(providerBarcode, providerName,
                                providerAddress);

                when(providerService.saveOrUpdate(mockProvider)).thenReturn(mockProvider);
                when(providerService.validBarcode("invalid Barcode")).thenReturn("Barcode must be a int number");

                MvcResult result = assertDoesNotThrow(() -> mockMvc
                                .perform(put("/updateProvider/invalid Barcode").contentType("application/json")
                                                .content(
                                                                "{\"barcode\":\"invalid Barcode\",\"name\":\"Test Provider\",\"address\":\"Test Provider Address\"}"))
                                .andExpect(status().isBadRequest()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString()
                                .contains("Barcode must be a int number"));
        }

        // The Provider Entity validation for editing/updating is the same as for
        // creating,
        // so I dont find it completely necessary to test all the different cases for
        // editing/updating

        @Test
        public void testDeleteProvider() throws Exception {
                String providerBarcode = "7777777";

                doNothing().when(providerService).delete(providerBarcode);
                when(providerService.existsById(providerBarcode)).thenReturn(true);
                when(providerService.validBarcode(providerBarcode)).thenReturn("success");

                MvcResult result = assertDoesNotThrow(
                                () -> mockMvc.perform(delete("/deleteProvider/" + providerBarcode))
                                                .andExpect(status().isOk()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString().contains("Provider deleted"));
        }

        @Test
        public void testDeleteNotExistingProvider() throws Exception {
                String providerBarcode = "7777777";

                doNothing().when(providerService).delete(providerBarcode);
                when(providerService.existsById(providerBarcode)).thenReturn(false);
                when(providerService.validBarcode(providerBarcode)).thenReturn("success");

                MvcResult result = assertDoesNotThrow(
                                () -> mockMvc.perform(delete("/deleteProvider/" + providerBarcode))
                                                .andExpect(status().isNotFound()).andReturn());

                // assert that the correct provider is returned
                assertTrue(result.getResponse().getContentAsString().contains("Provider not found"));
        }
}
