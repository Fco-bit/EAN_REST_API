package com.mercadona.barcoderestapi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.mercadona.barcoderestapi.model.Provider;
import com.mercadona.barcoderestapi.repository.ProviderRepository;

@Service
public class ProviderService {
    @Autowired
    private ProviderRepository providerRepository;

    //It only makes sense to cache the getById method since it is the only one who does not make a change if the DB
    @Cacheable("providers")
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
