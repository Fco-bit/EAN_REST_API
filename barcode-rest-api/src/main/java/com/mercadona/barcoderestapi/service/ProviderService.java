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

    // It only makes sense to cache the getById method since it is the only one who
    // does not make a change if the DB
    @Cacheable("providers")
    public Optional<Provider> getById(String id) {
        return providerRepository.findById(id);
    }

    public Provider saveOrUpdate(Provider provider) {
        return providerRepository.save(provider);
    }

    public void delete(String id) {
        providerRepository.deleteById(id);
    }

    // not cacheable since can lead to inconsistencies
    public Boolean existsById(String id) {
        return providerRepository.existsById(id);
    }

    // Validate the provider barcode
    public String validBarcode(String barcode) {
        try {
            Integer.parseInt(barcode);
        } catch (NumberFormatException e) {
            return "The barcode must be a valid int number";
        }
        if (barcode.length() != 7) {
            return "The barcode must be 7 characters";
        }
        return "success";
    }

}
