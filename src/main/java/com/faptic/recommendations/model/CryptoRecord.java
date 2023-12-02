package com.faptic.recommendations.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoRecord {

	private Instant timestamp;
	private String symbol;
	private double price;

}

