package com.mercadona.barcoderestapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mercadona.barcoderestapi.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}