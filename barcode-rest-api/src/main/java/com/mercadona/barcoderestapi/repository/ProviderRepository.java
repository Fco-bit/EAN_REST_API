package com.mercadona.barcoderestapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mercadona.barcoderestapi.model.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {

}
