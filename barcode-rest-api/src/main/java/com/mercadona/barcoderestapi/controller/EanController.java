package com.mercadona.barcoderestapi.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mercadona.barcoderestapi.model.DestinationEnum;
import com.mercadona.barcoderestapi.model.Product;
import com.mercadona.barcoderestapi.model.Provider;
import com.mercadona.barcoderestapi.service.EanService;
import com.mercadona.barcoderestapi.service.ProductService;
import com.mercadona.barcoderestapi.service.ProviderService;

@RestController
public class EanController {

    @Autowired
    private EanService eanService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProviderService providerService;

    @GetMapping("/getEan/{ean13}")
    @Cacheable("eanReader")
    public ResponseEntity<Object> getEan13(@PathVariable String ean13) throws Exception {

        // Validate the EAN
        String validatorMessage = eanService.eanValidator(ean13);
        if (validatorMessage != "success") {
            return ResponseEntity.badRequest().body(new HashMap<>() {
                {
                    put("message", validatorMessage);
                }
            });
        }
        // Get the EAN data (Product, Provider, Destination)
        HashMap<String, String> ean13Map = eanService.getEan13(ean13);

        // Validate the code from the provider and the product separately
        String validatorProductAndProviderMessage = eanService
                .validateProductAndProvideCodes(ean13Map.get("providerBarcode"), ean13Map.get("productBarcode"));
        if (validatorProductAndProviderMessage != "success") {
            return ResponseEntity.badRequest().body(new HashMap<>() {
                {
                    put("message", validatorProductAndProviderMessage);
                }
            });
        }

        // Get the Product, Provider and Destination from the DB
        Product product = productService.getById(ean13Map.get("productBarcode")).orElse(null);
        Provider provider = providerService.getById(ean13Map.get("providerBarcode")).orElse(null);
        DestinationEnum destination = DestinationEnum
                .getDestinationFromCode(Integer.parseInt(ean13Map.get("detination")));

        // Check if the data is null, if so return a exception
        if (product == null || provider == null || destination == null) {
            return ResponseEntity.badRequest().body(new HashMap<>() {
                {
                    put("message", "Could not resolve the EAN");
                }
            });
        }

        // Return the data associated with the EAN (barcode)
        return ResponseEntity.ok(new HashMap<>() {
            {
                put("product", product);
                put("provider", provider);
                put("destination", destination);
            }
        });

    }

    // Exception handler for the REST API this will handle all the exceptions thrown
    // in the differents endpoints of the API
    // and will return a JSON with the error message, it will be interesting to be
    // more precise and handle different exceptions
    // with different and more precise error messages
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        // Build the response entity with the Exception error message
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
            {
                put("error message", "Some error happened");
            }
        });
    }
}
