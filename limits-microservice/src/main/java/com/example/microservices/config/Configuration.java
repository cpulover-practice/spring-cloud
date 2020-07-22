package com.example.microservices.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties("limits-service")
@Component
@Data
public class Configuration {
	private int max;
	private int min;

}
