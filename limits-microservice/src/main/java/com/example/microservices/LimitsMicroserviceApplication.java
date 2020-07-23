package com.example.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableHystrix
public class LimitsMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LimitsMicroserviceApplication.class, args);
	}

}
