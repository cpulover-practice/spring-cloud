package com.cpulover.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CurrencyExchangeMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyExchangeMicroserviceApplication.class, args);
	}

}
