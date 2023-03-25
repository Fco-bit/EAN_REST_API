package com.mercadona.barcoderestapi.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String index() {

        return "Welcome to the BEST Barcode REST API!";
    }

    @GetMapping("/getProduct/{barcode}")
    public ResponseEntity<Object> getProduct(@PathVariable Integer barcode) {
        Product product = productService.getById(barcode).orElse(null);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.ok(new HashMap<>() {
                {
                    put("message", "Product not found");
                }
            });
        }
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Object> addProduct(@Valid @RequestBody Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //get the errors messages 
            List<String> errors = bindingResult.getAllErrors().stream().map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", errors);
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

    @PostMapping("/updateProduct/{barcode}")
    public ResponseEntity<Object> updateProduct(@PathVariable Integer barcode, @Valid @RequestBody Product product,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //get the errors messages 
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
            return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<>() {
                {
                    put("message", saved_product);
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

    @GetMapping("/deleteProduct/{barcode}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Integer barcode) {
        if (productService.existsById(barcode)) {
            productService.delete(barcode);
            return ResponseEntity.status(HttpStatus.OK).body(new HashMap<>() {
                {
                    put("message", "Product deleted");
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
    public ResponseEntity<Object> getProvider(@PathVariable Integer id) {
        if (providerService.existsById(id)) {
            return ResponseEntity.ok(providerService.getById(id));
        } else {
            return ResponseEntity.ok(new HashMap<>() {
                {
                    put("message", "Provider not found");
                }
            });
        }
    }

    @PostMapping("/addProvider")
    public ResponseEntity<Object> addProvider(@Valid @RequestBody Provider provider, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //get the errors messages 
            List<String> errors = bindingResult.getAllErrors().stream().map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", errors);
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

    @PostMapping("/updateProvider/{barcode}")
    public ResponseEntity<Object> updateProvider(@PathVariable Integer barcode, @Valid @RequestBody Provider provider,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //get the errors messages 
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

    @GetMapping("/deleteProvider/{id}")
    public ResponseEntity<Object> deleteProvider(@PathVariable Integer id) {
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
}
