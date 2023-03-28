package com.mercadona.barcoderestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BarcodeRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarcodeRestApiApplication.class, args);
	}

}
