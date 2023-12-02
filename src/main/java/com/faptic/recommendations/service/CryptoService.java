package com.faptic.recommendations.service;

import com.faptic.recommendations.model.CryptoRecord;
import com.faptic.recommendations.model.CryptoStats;
import com.faptic.recommendations.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoService {

	private final CryptoRepository cryptoRepository;

	// Requirement 1:
	// return a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)
	public List<CryptoStats> getAllCryptoStatsByNormalizedRange(Instant startDate, Instant endDate) {
		return cryptoRepository.getCryptoRecords().values().stream()
				.flatMap(List::stream)
				.filter(record -> !record.getTimestamp().isBefore(startDate) && !record.getTimestamp().isAfter(endDate))
				.collect(Collectors.groupingBy(CryptoRecord::getSymbol))
				.entrySet().stream()
				.map(entry -> calculateStats(entry.getValue()))
				.sorted(Comparator.comparing(CryptoStats::getNormalizedRange).reversed())
				.collect(Collectors.toList());
	}

	// Requirement 2:
	// return the oldest/newest/min/max values for a requested crypto
	public CryptoStats getCryptoStatsForSymbol(String symbol, Instant startDate, Instant endDate) {
		List<CryptoRecord> filteredRecords = cryptoRepository.getCryptoRecords().getOrDefault(symbol, List.of()).stream()
				.filter(record -> !record.getTimestamp().isBefore(startDate) && !record.getTimestamp().isAfter(endDate))
				.collect(Collectors.toList());

		return calculateStats(filteredRecords);
	}

	// Requirement 3:
	// return the crypto with the highest normalized range for a specific day
	public CryptoStats getCryptoWithHighestRangeForDay(Instant day) {
		return cryptoRepository.getCryptoRecords().values().stream()
				.flatMap(List::stream)
				.filter(record -> record.getTimestamp().truncatedTo(ChronoUnit.DAYS).equals(day.truncatedTo(ChronoUnit.DAYS)))
				.collect(Collectors.groupingBy(CryptoRecord::getSymbol))
				.entrySet().stream()
				.map(entry -> calculateStats(entry.getValue()))
				.max(Comparator.comparing(CryptoStats::getNormalizedRange))
				.orElse(null);
	}

	public boolean isKnownSymbol(String symbol) {
		return cryptoRepository.getCryptoRecords().containsKey(symbol.toUpperCase());
	}

	private CryptoStats calculateStats(List<CryptoRecord> records) {
		if (records.isEmpty()) {
			return new CryptoStats();
		}

		String symbol = records.get(0).getSymbol();

		double minPrice = records.stream().min(Comparator.comparingDouble(CryptoRecord::getPrice)).orElseThrow().getPrice();
		double maxPrice = records.stream().max(Comparator.comparingDouble(CryptoRecord::getPrice)).orElseThrow().getPrice();
		Instant oldest = records.stream().min(Comparator.comparing(CryptoRecord::getTimestamp)).orElseThrow().getTimestamp();
		Instant newest = records.stream().max(Comparator.comparing(CryptoRecord::getTimestamp)).orElseThrow().getTimestamp();

		return new CryptoStats(symbol, oldest, newest, minPrice, maxPrice);
	}


}
