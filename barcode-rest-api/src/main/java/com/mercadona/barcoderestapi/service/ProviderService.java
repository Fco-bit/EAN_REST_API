package com.mercadona.barcoderestapi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercadona.barcoderestapi.model.Provider;
import com.mercadona.barcoderestapi.repository.ProviderRepository;

@Service
public class ProviderService {
    @Autowired
    private ProviderRepository providerRepository;

    public Optional<Provider> getById(int id) {
        return providerRepository.findById(id);
    }

    public Provider saveOrUpdate(Provider provider) {
        return providerRepository.save(provider);
    }

    public void delete(int id) {
        providerRepository.deleteById(id);
    }

    public Boolean existsById(int id) {
        return providerRepository.existsById(id);
    }
}
