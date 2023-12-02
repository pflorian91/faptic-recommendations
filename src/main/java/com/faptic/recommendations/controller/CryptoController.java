package com.faptic.recommendations.controller;

import com.faptic.recommendations.exception.DataNotFoundException;
import com.faptic.recommendations.exception.SymbolNotFoundException;
import com.faptic.recommendations.model.CryptoStats;
import com.faptic.recommendations.service.CryptoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cryptos")
@Tag(name = "CryptoController", description = "The CryptoController provides statistical data about cryptocurrencies")
public class CryptoController {

	@Value("${faptic.timeframe.startDate}")
	private String startDate;

	@Value("${faptic.timeframe.endDate}")
	private String endDate;

	private final CryptoService cryptoService;

	@GetMapping("/stats")
	@Operation(summary = "Get statistics of all cryptos within a date range", description = "Provide an optional date range to filter the statistics")
	public ResponseEntity<List<CryptoStats>> getCryptoStats(
			@Parameter(description = "Start date for the statistics period", example = "2022-01-01")
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@Parameter(description = "End date for the statistics period", example = "2022-01-31")
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
	) {
		if (startDate == null) {
			startDate = LocalDate.parse(this.startDate);
		}
		if (endDate == null) {
			endDate = LocalDate.parse(this.endDate);
		}

		Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant endInstant = endDate.atStartOfDay().toInstant(ZoneOffset.UTC);

		log.debug("Query interval {} - {} ", startInstant, endInstant);

		return ResponseEntity.ok(cryptoService.getAllCryptoStatsByNormalizedRange(startInstant, endInstant));
	}

	@GetMapping("/stats/{symbol}")
	@Operation(summary = "Get statistics for a specific crypto symbol within a date range",
			description = "Fetch statistics for a particular cryptocurrency symbol over a specified date range. Returns 404 if the symbol is not supported.")
	public ResponseEntity<CryptoStats> getCryptoStatsForSymbol(
			@Parameter(description = "The symbol of the cryptocurrency to fetch statistics for", example = "BTC")
			@PathVariable String symbol,
			@Parameter(description = "Start date for the statistics period", example = "2022-01-01")
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@Parameter(description = "End date for the statistics period", example = "2022-01-31")
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
	) {
		// consideration
		// safeguard recommendations service endpoints from not currently supported cryptos
		if (cryptoService.isKnownSymbol(symbol)) {

			if (startDate == null) {
				startDate = LocalDate.parse(this.startDate);
			}
			if (endDate == null) {
				endDate = LocalDate.parse(this.endDate);
			}

			Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
			Instant endInstant = endDate.atStartOfDay().toInstant(ZoneOffset.UTC);

			log.debug("Query interval {} - {} ", startInstant, endInstant);

			return ResponseEntity.ok(cryptoService.getCryptoStatsForSymbol(symbol, startInstant, endInstant));
		} else {
			throw new SymbolNotFoundException(symbol);
		}
	}

	@GetMapping("/highest-range/{date}")
	@Operation(summary = "Get the crypto with the highest normalized range for a specific day",
			description = "Retrieve the cryptocurrency that had the highest normalized range for a given day. Returns 404 if no data is available for the given date.")
	public ResponseEntity<CryptoStats> getCryptoWithHighestRangeForDay(
			@Parameter(description = "The date to fetch the cryptocurrency with the highest normalized range", example = "2022-01-01")
			@PathVariable String date) {
		Instant day = LocalDate.parse(date).atStartOfDay().toInstant(ZoneOffset.UTC);

		CryptoStats rangeForDay = cryptoService.getCryptoWithHighestRangeForDay(day);

		if (rangeForDay != null) {
			return ResponseEntity.ok(rangeForDay);
		} else {
			throw new DataNotFoundException("No data available for the date: " + date);
		}
	}
}
