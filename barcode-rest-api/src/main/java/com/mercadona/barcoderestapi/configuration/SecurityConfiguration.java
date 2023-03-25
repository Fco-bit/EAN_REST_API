package com.mercadona.barcoderestapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/deleteProduct/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/getProvider/**")).permitAll()
                        .requestMatchers("/addProvider").permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/updateProvider/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/deleteProvider/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/getEan/**")).permitAll())
                .headers(headers -> headers.frameOptions().disable())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                        .ignoringRequestMatchers("/addProduct")
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/updateProduct/**"))
                        .ignoringRequestMatchers("/addProvider")
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/updateProvider/**")));

        return http.build();
    }

}
