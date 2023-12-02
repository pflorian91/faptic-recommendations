package com.faptic.recommendations.model;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CryptoStats {

	private String symbol;
	private Instant oldestTimestamp;
	private Instant newestTimestamp;
	private double minPrice;
	private double maxPrice;

	public double getNormalizedRange() {
		return (maxPrice - minPrice) / minPrice;
	}

}

