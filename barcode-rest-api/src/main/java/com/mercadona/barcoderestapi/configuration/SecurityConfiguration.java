package com.mercadona.barcoderestapi.configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/getProduct/**")).permitAll()
                        .requestMatchers("/addProduct").permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/updateProduct/**")).permitAll()
                        .requestMatchers(HttpMethod.DELETE, "deleteProduct/**").permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/getProvider/**")).permitAll()
                        .requestMatchers("/addProvider").permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/updateProvider/**")).permitAll()
                        .requestMatchers(HttpMethod.DELETE, "deleteProvider/**").permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/getEan/**")).permitAll())
                .exceptionHandling().authenticationEntryPoint((request, response, accessDeniedException) -> {
                    // Handle access denied exception
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{ \"error\": \"Forbidden Endpoint\" }");
                }).and()
                .headers(headers -> headers.frameOptions().disable())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                        .ignoringRequestMatchers("/addProduct")
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/updateProduct/**"))
                        .ignoringRequestMatchers("/addProvider")
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/updateProvider/**"))
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/deleteProduct/**"))
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/deleteProvider/**")));

        return http.build();
    }

    @Bean
    public CacheManager cacheManager() {
        String[] cacheNames = { "products", "providers", "eanReader", "getProduct", "getProvider" };
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS));
        cacheManager.setCacheNames(Arrays.asList(cacheNames));
        return cacheManager;
    }

}
