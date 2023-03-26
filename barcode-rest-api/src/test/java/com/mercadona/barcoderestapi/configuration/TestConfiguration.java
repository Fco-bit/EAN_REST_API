package com.mercadona.barcoderestapi.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfiguration {
    
    @Bean
    public CacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}
