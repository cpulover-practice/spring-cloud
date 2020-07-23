package com.example.microservices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.microservices.config.Configuration;
import com.example.microservices.entity.LimitConfiguration;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class LimitConfigRestController {

	@Autowired
	private Configuration configuration;

	@GetMapping("/limits")
	public LimitConfiguration getLimitConfiguration() {
		return new LimitConfiguration(configuration.getMax(), configuration.getMin());
	}

	@GetMapping("/fault-tolerance-example")
	@HystrixCommand(fallbackMethod = "fallbackGetLimitConfig")
	public LimitConfiguration getLimitConfigurationButFail() {
		throw new RuntimeException("Booo...");
	}

	public LimitConfiguration fallbackGetLimitConfig() {
		return new LimitConfiguration(3333, 33);
	}
}
