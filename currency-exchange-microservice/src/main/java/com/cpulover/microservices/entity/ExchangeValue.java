package com.cpulover.microservices.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeValue {
	private long id;
	private String from;
	private String to;
	private BigDecimal conversionMuliple;
	private int port;
}
