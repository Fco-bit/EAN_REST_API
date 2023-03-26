package com.mercadona.barcoderestapi.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercadona.barcoderestapi.model.Product;
import com.mercadona.barcoderestapi.model.Provider;
import com.mercadona.barcoderestapi.service.ProductService;
import com.mercadona.barcoderestapi.service.ProviderService;

import jakarta.validation.Valid;

@RestController
public class CrudController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProviderService providerService;

    @GetMapping("/")
    public String index() throws Exception {

        return "Welcome to the BEST Barcode REST API!";
    }

    @GetMapping("/getProduct/{barcode}")
    public ResponseEntity<Object> getProduct(@PathVariable String barcode) throws Exception {

        // Check if the barcode valid
        String msgResult = productService.validBarcode(barcode);
        if (msgResult != "success") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", msgResult);
                }
            });
        }
        Product product = productService.getById(barcode).orElse(null);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", "Product not found");
                }
            });
        }

    }

    @PostMapping("/addProduct")
    public ResponseEntity<Object> addProduct(@Valid @RequestBody Product product, BindingResult bindingResult)
            throws Exception {
        if (bindingResult.hasErrors()) {
            // get the errors messages
            List<String> errors = bindingResult.getAllErrors().stream().map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", errors);
                }
            });
        }
        if (productService.existsById(product.getBarcode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", "Provider with the barcode " + product.getBarcode() + " already exists");
                }
            });
        }
        Product saved_product = productService.saveOrUpdate(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<>() {
            {
                put("message", saved_product);
            }
        });

    }

    @PutMapping("/updateProduct/{barcode}")
    public ResponseEntity<Object> updateProduct(@PathVariable String barcode, @Valid @RequestBody Product product,
            BindingResult bindingResult) throws Exception {
        // Check if the barcode valid
        String msgResult = productService.validBarcode(barcode);
        if (msgResult != "success") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", msgResult);
                }
            });
        }
        if (bindingResult.hasErrors()) {
            // get the errors messages
            List<String> errors = bindingResult.getAllErrors().stream().map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", errors);
                }
            });
        }

        if (productService.existsById(barcode)) {

            Product saved_product = productService.saveOrUpdate(product);
            if (barcode != product.getBarcode()) {
                productService.delete(barcode);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new HashMap<>() {
                {
                    put("message", "Product updated successfully");
                    put("product", saved_product);
                }
            });
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<>() {
                {
                    put("message", "Product not found");
                }
            });
        }
    }

    @DeleteMapping("/deleteProduct/{barcode}")
    public ResponseEntity<Object> deleteProduct(@PathVariable String barcode) throws Exception {
        // Check if the barcode valid
        String msgResult = productService.validBarcode(barcode);
        if (msgResult != "success") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", msgResult);
                }
            });
        }

        if (productService.existsById(barcode)) {
            productService.delete(barcode);
            return ResponseEntity.status(HttpStatus.OK).body(new HashMap<>() {
                {
                    put("message", "Product deleted successfully");
                }
            });
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<>() {
                {
                    put("message", "Product not found");
                }
            });
        }
    }

    @GetMapping("/getProvider/{id}")
    public ResponseEntity<Object> getProvider(@PathVariable String id) throws Exception {
        // Check if the barcode valid
        String msgResult = providerService.validBarcode(id);
        if (msgResult != "success") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", msgResult);
                }
            });
        }

        Provider provider = providerService.getById(id).orElse(null);
        if (provider != null) {
            return ResponseEntity.ok(provider);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<>() {
                {
                    put("message", "Provider not found");
                }
            });
        }

    }

    @PostMapping("/addProvider")
    public ResponseEntity<Object> addProvider(@Valid @RequestBody Provider provider, BindingResult bindingResult)
            throws Exception {
        if (bindingResult.hasErrors()) {
            // get the errors messages
            List<String> errors = bindingResult.getAllErrors().stream().map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", errors);
                }
            });
        }
        if (providerService.existsById(provider.getBarcode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", "Provider with the barcode " + provider.getBarcode() + " already exists");
                }
            });
        }

        Provider saved_provider = providerService.saveOrUpdate(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<>() {
            {
                put("message", saved_provider);
            }
        });

    }

    @PutMapping("/updateProvider/{barcode}")
    public ResponseEntity<Object> updateProvider(@PathVariable String barcode, @Valid @RequestBody Provider provider,
            BindingResult bindingResult) throws Exception {
        // Check if the barcode valid
        String msgResult = providerService.validBarcode(barcode);
        if (msgResult != "success") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", msgResult);
                }
            });
        }
        if (bindingResult.hasErrors()) {
            // get the errors messages
            List<String> errors = bindingResult.getAllErrors().stream().map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", errors);
                }
            });
        }

        if (providerService.existsById(barcode)) {
            Provider saved_provider = providerService.saveOrUpdate(provider);
            if (barcode != provider.getBarcode()) {
                providerService.delete(barcode);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<>() {
                {
                    put("message", saved_provider);
                }
            });
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<>() {
                {
                    put("message", "Provider not found");
                }
            });
        }
    }

    @DeleteMapping("/deleteProvider/{id}")
    public ResponseEntity<Object> deleteProvider(@PathVariable String id) throws Exception {
        // Check if the barcode valid
        String msgResult = providerService.validBarcode(id);
        if (msgResult != "success") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", msgResult);
                }
            });
        }
        if (providerService.existsById(id)) {
            providerService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(new HashMap<>() {
                {
                    put("message", "Provider deleted");
                }
            });
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<>() {
                {
                    put("message", "Provider not found");
                }
            });
        }
    }

    // Exception handler for the REST API this will handle all the exceptions thrown
    // in the differents endpoints of the API
    // and will return a JSON with the error message, it will be interesting to be
    // more precise and handle different exceptions
    // with different error messages
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
