package com.mercadona.barcoderestapi.controller;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    public String index() throws Exception {

        return "Welcome to the BEST Barcode REST API!";
    }

    @GetMapping("/getProduct/{barcode}")
    public ResponseEntity<Object> getProduct(@PathVariable String barcode) throws Exception {
        try {
            Integer intBarcode = Integer.parseInt(barcode);
            Product product = productService.getById(intBarcode).orElse(null);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                    {
                        put("message", "Product not found");
                    }
                });
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", "Barcode must be a int number");
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

        Product saved_product = productService.saveOrUpdate(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<>() {
            {
                put("message", saved_product);
            }
        });

    }

    @PostMapping("/updateProduct/{barcode}")
    public ResponseEntity<Object> updateProduct(@PathVariable Integer barcode, @Valid @RequestBody Product product,
            BindingResult bindingResult) throws Exception {
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
    public ResponseEntity<Object> deleteProduct(@PathVariable Integer barcode) throws Exception {
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
    public ResponseEntity<Object> getProvider(@PathVariable String id) throws Exception {
        try {
            Integer intBarcode = Integer.parseInt(id);
            Provider provider = providerService.getById(intBarcode).orElse(null);
            if (provider != null) {
                return ResponseEntity.ok(provider);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                    {
                        put("message", "provider not found");
                    }
                });
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
                {
                    put("message", "Barcode must be a int number");
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

        Provider saved_provider = providerService.saveOrUpdate(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<>() {
            {
                put("message", saved_provider);
            }
        });

    }

    @PostMapping("/updateProvider/{barcode}")
    public ResponseEntity<Object> updateProvider(@PathVariable Integer barcode, @Valid @RequestBody Provider provider,
            BindingResult bindingResult) throws Exception {
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

    @GetMapping("/deleteProvider/{id}")
    public ResponseEntity<Object> deleteProvider(@PathVariable Integer id) throws Exception {
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

    // Exception handler for the REST API this will handle all the exceptions thrown in the differents endpoints of the API
    // and will return a JSON with the error message, it will be interesting to be more precise and handle different exceptions
    // with different error messages
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        // Build the response entity with the Exception error message 
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>() {
            {
                put("error message", "Some error happened" );
            }
        });
    }
}
