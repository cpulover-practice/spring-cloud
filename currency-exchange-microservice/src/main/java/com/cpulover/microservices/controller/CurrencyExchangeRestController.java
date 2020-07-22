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

@RestController
public class CurrencyExchangeRestController {

	@Autowired
	private Environment environment; // to get the port
	
	//@Value("${local.server.port}")
	//@LocalServerPort
	private int port;

	@PostConstruct // execute after injection
	public void init() {
		if (!(environment.getProperty("server.port") == null)) {
			port = Integer.parseInt(environment.getProperty("server.port"));
		}
		System.out.println(port);
	}

	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public ExchangeValue getExchangeValue(@PathVariable String from, @PathVariable String to) {
		return new ExchangeValue(69L, from, to, BigDecimal.valueOf(6969), port);
	}

}
