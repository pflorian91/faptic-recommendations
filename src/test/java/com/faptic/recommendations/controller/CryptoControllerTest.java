package com.faptic.recommendations.controller;

import com.faptic.recommendations.model.CryptoStats;
import com.faptic.recommendations.service.CryptoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CryptoController.class)
class CryptoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CryptoService cryptoService;

	@Test
	public void givenThereIsNoData_whenTheCryptoStatsEndpointIsCalled_thenAnEmptyListIsReturned() throws Exception {
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-02-01T00:00:00Z");
		when(cryptoService.getAllCryptoStatsByNormalizedRange(startDate, endDate)).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/api/cryptos/stats")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	public void givenThereIsData_whenTheCryptoStatsEndpointIsCalled_thenTheDataIsReturned() throws Exception {
		CryptoStats cryptoStats1 = CryptoStats.builder()
				.symbol("BTC")
				.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
				.newestTimestamp(Instant.parse("2022-01-31T23:59:59Z"))
				.minPrice(30000.0)
				.maxPrice(40000.0)
				.build();
		CryptoStats cryptoStats2 = CryptoStats.builder()
				.symbol("ETH")
				.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
				.newestTimestamp(Instant.parse("2022-01-31T23:59:59Z"))
				.minPrice(1000.0)
				.maxPrice(2000.0)
				.build();

		List<CryptoStats> mockCryptoStatsList = Arrays.asList(cryptoStats1, cryptoStats2);
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-02-01T00:00:00Z");
		when(cryptoService.getAllCryptoStatsByNormalizedRange(startDate, endDate)).thenReturn(mockCryptoStatsList);
		String expectedJson = objectMapper.writeValueAsString(mockCryptoStatsList);

		mockMvc.perform(get("/api/cryptos/stats")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}

	@Test
	public void givenDateRange_whenTheCryptoStatsEndpointIsCalled_thenTheDataIsReturned() throws Exception {
		CryptoStats cryptoStats1 = CryptoStats.builder()
				.symbol("BTC")
				.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
				.newestTimestamp(Instant.parse("2022-01-31T23:59:59Z"))
				.minPrice(30000.0)
				.maxPrice(40000.0)
				.build();
		CryptoStats cryptoStats2 = CryptoStats.builder()
				.symbol("ETH")
				.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
				.newestTimestamp(Instant.parse("2022-01-31T23:59:59Z"))
				.minPrice(1000.0)
				.maxPrice(2000.0)
				.build();

		List<CryptoStats> mockCryptoStatsList = Arrays.asList(cryptoStats1, cryptoStats2);
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-02-01T00:00:00Z");
		when(cryptoService.getAllCryptoStatsByNormalizedRange(startDate, endDate)).thenReturn(mockCryptoStatsList);
		String expectedJson = objectMapper.writeValueAsString(mockCryptoStatsList);

		mockMvc.perform(get("/api/cryptos/stats")
						.param("startDate", "2022-01-01")
						.param("endDate", "2022-02-01")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}

	@Test
	public void whenGeneralException_thenInternalServerErrorResponse() throws Exception {
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-02-01T00:00:00Z");
		doThrow(new RuntimeException("Unexpected error")).when(cryptoService).getAllCryptoStatsByNormalizedRange(startDate, endDate);

		mockMvc.perform(get("/api/cryptos/stats")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string("An unexpected error occurred"));
	}

	@Test
	public void givenInvalidDateFormat_whenCalledStatsEndpoint_thenBadRequest() throws Exception {
		String invalidDate = "2022-02-30";

		mockMvc.perform(get("/api/cryptos/stats")
						.param("startDate", invalidDate)
						.param("endDate", "2022-03-01")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void givenInvalidDateFormatForStatsForSymbolEndpoint_whenCalled_thenBadRequest() throws Exception {
		String symbol = "BTC";
		String invalidDate = "2022-02-30";

		mockMvc.perform(get("/api/cryptos/stats/" + symbol)
						.param("startDate", "2021-02-01")
						.param("endDate", invalidDate)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void givenAValidSymbolIsProvided_whenStatsForSymbolEndpointIsCalled_thenCorrectStatsAreReturned() throws Exception {
		String symbol = "BTC";
		CryptoStats mockStats = CryptoStats.builder()
				.symbol(symbol)
				.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
				.newestTimestamp(Instant.parse("2022-01-31T23:59:59Z"))
				.minPrice(30000.0)
				.maxPrice(40000.0)
				.build();

		when(cryptoService.isKnownSymbol(symbol)).thenReturn(true);
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-02-01T00:00:00Z");
		when(cryptoService.getCryptoStatsForSymbol(symbol, startDate, endDate)).thenReturn(mockStats);

		String expectedJson = objectMapper.writeValueAsString(mockStats);

		mockMvc.perform(get("/api/cryptos/stats/" + symbol)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}

	@Test
	public void givenAValidSymbolAndDateRange_whenStatsForSymbolEndpointIsCalled_thenCorrectStatsAreReturned() throws Exception {
		String symbol = "BTC";
		CryptoStats mockStats = CryptoStats.builder()
				.symbol(symbol)
				.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
				.newestTimestamp(Instant.parse("2022-01-31T23:59:59Z"))
				.minPrice(30000.0)
				.maxPrice(40000.0)
				.build();

		when(cryptoService.isKnownSymbol(symbol)).thenReturn(true);
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-02-01T00:00:00Z");
		when(cryptoService.getCryptoStatsForSymbol(symbol, startDate, endDate)).thenReturn(mockStats);

		String expectedJson = objectMapper.writeValueAsString(mockStats);

		mockMvc.perform(get("/api/cryptos/stats/" + symbol)
						.param("startDate", "2022-01-01")
						.param("endDate", "2022-02-01")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}

	@Test
	public void givenAnInvalidSymbolIsProvided_whenStatsForSymbolEndpointIsCalled_thenNotFoundIsReturned() throws Exception {
		String symbol = "UNKNOWN";

		when(cryptoService.isKnownSymbol(symbol)).thenReturn(false);

		mockMvc.perform(get("/api/cryptos/stats/" + symbol)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void givenDataExists_whenEndpointIsCalledWithValidDate_thenCryptoWithHighestRangeForDayIsReturned() throws Exception {
		String date = "2022-01-01";
		CryptoStats cryptoStats = CryptoStats.builder()
				.symbol("BTC")
				.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
				.newestTimestamp(Instant.parse("2022-01-31T23:59:59Z"))
				.minPrice(30000.0)
				.maxPrice(40000.0)
				.build();

		String expectedJson = objectMapper.writeValueAsString(cryptoStats);

		when(cryptoService.getCryptoWithHighestRangeForDay(any(Instant.class))).thenReturn(cryptoStats);

		mockMvc.perform(get("/api/cryptos/highest-range/" + date)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}

	@Test
	public void whenInvalidDateFormat_thenBadRequestResponse() throws Exception {
		String invalidDate = "invalid-date-format";

		mockMvc.perform(get("/api/cryptos/highest-range/" + invalidDate)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Invalid date format")));
	}

	@Test
	public void whenNoDataForGivenDay_thenNotFoundResponse() throws Exception {
		String date = "2012-01-01";
		when(cryptoService.getCryptoWithHighestRangeForDay(any(Instant.class))).thenReturn(null);

		mockMvc.perform(get("/api/cryptos/highest-range/" + date)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}