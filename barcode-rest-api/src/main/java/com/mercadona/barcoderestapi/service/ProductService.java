package com.mercadona.barcoderestapi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.mercadona.barcoderestapi.model.Product;
import com.mercadona.barcoderestapi.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    //It only makes sense to cache the getById method since it is the only one who does not make a change if the DB
    @Cacheable("products")
    public Optional<Product> getById(String id) {
        return productRepository.findById(id);
    }

    public Product saveOrUpdate(Product product) {
        return productRepository.save(product);
    }

    public void delete(String id) {
        productRepository.deleteById(id);
    }

    public Boolean existsById(String id) {
        return productRepository.existsById(id);
    }

    public String validBarcode(String barcode) {
        try {
            Integer.parseInt(barcode);
        } catch (NumberFormatException e) {
            return "The barcode must be a valid int number";
        }
        if (barcode.length() != 5) {
            return "The barcode must be 5 characters";
        }
        return "success";
    }

}