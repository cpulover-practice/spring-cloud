package com.cpulover.microservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cpulover.microservices.entity.ExchangeValue;

public interface ExchangeValueRepository extends JpaRepository<ExchangeValue, Long> {
	ExchangeValue findByFromAndTo(String from, String to);
}
