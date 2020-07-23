package com.cpulover.microservices.controller;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cpulover.microservices.entity.ExchangeValue;
import com.cpulover.microservices.repository.ExchangeValueRepository;

@RestController
public class CurrencyExchangeRestController {
	
	@Autowired
	ExchangeValueRepository exchangeValueRepository;

	@Autowired
	private Environment environment; // to get the port

	// @Value("${local.server.port}")
	// @LocalServerPort
	private int port;

	@PostConstruct // execute after injection
	public void init() {
		if (!(environment.getProperty("server.port") == null)) {
			port = Integer.parseInt(environment.getProperty("server.port"));
		}
	}

	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public ExchangeValue getExchangeValue(@PathVariable String from, @PathVariable String to) {
		ExchangeValue exchangeValue = exchangeValueRepository.findByFromAndTo(from, to);
		exchangeValue.setPort(port);
		return exchangeValue;

	}

}
