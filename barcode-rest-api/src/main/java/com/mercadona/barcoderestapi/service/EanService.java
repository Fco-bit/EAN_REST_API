package com.mercadona.barcoderestapi.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EanService {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProviderService providerService;

    // This method take the 13 digits of the EAN and divides them into 3 parts
    // (provider, product and destination)
    public HashMap<String, String> getEan13(String ean13) {
        HashMap<String, String> ean13Map = new HashMap<String, String>();
        String providerBarcode = ean13.substring(0, 7);
        System.out.println(providerBarcode);
        String productBarcode = ean13.substring(7, 12);
        String destination = ean13.substring(12, 13);
        ean13Map.put("providerBarcode", providerBarcode);
        ean13Map.put("productBarcode", productBarcode);
        ean13Map.put("detination", destination);
        return ean13Map;
    }

    // Validate the EAN
    public String eanValidator(String ean13) {
        Long eanNumber = -1L;
        if (ean13.length() != 13) {
            return "EAN must be 13 digits long";
        }
        try {
            eanNumber = Long.parseLong(ean13);
        } catch (Exception e) {
            return "EAN must be a number";
        }
        if (eanNumber < 0) {
            return "EAN must be a positive number";
        }
        return "success";

    }

    public String validateProductAndProvideCodes(String providerBarcode, String productBarcode) {
        String validatorMessage = providerService.validBarcode(providerBarcode);
        if (validatorMessage != "success") {
            return validatorMessage;
        }
        validatorMessage = productService.validBarcode(productBarcode);
        if (validatorMessage != "success") {
            return validatorMessage;
        }
        return "success";
    }

}
