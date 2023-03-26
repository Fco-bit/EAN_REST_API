package com.mercadona.barcoderestapi.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> getEan13(@PathVariable String ean13) {

        // Validate the EAN
        String validatorMessage = eanService.eanValidator(ean13);
        if (validatorMessage != "Success") {
            return ResponseEntity.badRequest().body(new HashMap<>() {
                {
                    put("message", validatorMessage);
                }
            });
        }
        // Get the EAN data (Product, Provider, Destination)
        HashMap<String, String> ean13Map = eanService.getEan13(ean13);
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

}
