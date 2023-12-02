package com.faptic.recommendations.service;

import com.faptic.recommendations.model.CryptoStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CryptoServiceTest {

	@Autowired
	private CryptoService cryptoService;

	@Test
	void getAllCryptoStatsByNormalizedRange() {
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-01-31T23:59:59Z");
		List<CryptoStats> cryptoStatsByNormalizedRange = cryptoService.getAllCryptoStatsByNormalizedRange(startDate, endDate);

		assertThat(cryptoStatsByNormalizedRange).contains(
				CryptoStats.builder()
						.symbol("BTC")
						.oldestTimestamp(Instant.parse("2022-01-01T04:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-31T20:00:00Z"))
						.minPrice(33276.59)
						.maxPrice(47722.66)
						.build(),
				CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-01T06:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-31T17:00:00Z"))
						.minPrice(103.4)
						.maxPrice(151.5)
						.build(),
				CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-01T08:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-31T20:00:00Z"))
						.minPrice(2336.52)
						.maxPrice(3828.11)
						.build(),
				CryptoStats.builder()
						.symbol("XRP")
						.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-31T01:00:00Z"))
						.minPrice(0.5616)
						.maxPrice(0.8458)
						.build(),
				CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-01T05:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-31T19:00:00Z"))
						.minPrice(0.129)
						.maxPrice(0.1941)
						.build()
		);
	}

	@Test
	void getAllCryptoStatsByNormalizedRangeWithShorterInterval() {
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-01-11T23:59:59Z");
		List<CryptoStats> cryptoStatsByNormalizedRange = cryptoService.getAllCryptoStatsByNormalizedRange(startDate, endDate);

		assertThat(cryptoStatsByNormalizedRange).contains(
				CryptoStats.builder()
						.symbol("BTC")
						.oldestTimestamp(Instant.parse("2022-01-01T04:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-11T16:00:00Z"))
						.minPrice(40774.01)
						.maxPrice(47722.66)
						.build(),
				CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-01T06:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-11T06:00:00Z"))
						.minPrice(126.0)
						.maxPrice(151.3)
						.build(),
				CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-01T08:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-11T14:00:00Z"))
						.minPrice(3009.22)
						.maxPrice(3828.11)
						.build(),
				CryptoStats.builder()
						.symbol("XRP")
						.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-11T16:00:00Z"))
						.minPrice(0.7226)
						.maxPrice(0.8458)
						.build(),
				CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-01T05:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-11T10:00:00Z"))
						.minPrice(0.1441)
						.maxPrice(0.1731)
						.build()
		);
	}

	@Test
	void getCryptoStatsForSymbol() {
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-02-01T00:00:00Z");
		CryptoStats xrp = cryptoService.getCryptoStatsForSymbol("XRP", startDate, endDate);
		assertThat(xrp).isEqualTo(CryptoStats.builder()
				.symbol("XRP")
				.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
				.newestTimestamp(Instant.parse("2022-01-31T01:00:00Z"))
				.minPrice(0.5616)
				.maxPrice(0.8458)
				.build());
	}

	@Test
	void getCryptoStatsForSymbolWithShorterInterval() {
		Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
		Instant endDate = Instant.parse("2022-01-11T00:00:00Z");

		CryptoStats xrp = cryptoService.getCryptoStatsForSymbol("DOGE", startDate, endDate);

		assertThat(xrp).isEqualTo(CryptoStats.builder()
				.symbol("DOGE")
				.oldestTimestamp(Instant.parse("2022-01-01T05:00:00Z"))
				.newestTimestamp(Instant.parse("2022-01-10T05:00:00Z"))
				.minPrice(0.1505)
				.maxPrice(0.1731)
				.build());
	}

	@ParameterizedTest
	@MethodSource("dateToCryptoProvider")
	void getCryptoWithHighestRangeForDay(Instant date, CryptoStats expectedCrypto) {
		CryptoStats result = cryptoService.getCryptoWithHighestRangeForDay(date);
		assertThat(result).as("Date %s expected crypto %s", date, expectedCrypto).isEqualTo(expectedCrypto);
	}

	public static Stream<Arguments> dateToCryptoProvider() {
		return Stream.of(
				Arguments.of(Instant.parse("2021-12-10T00:00:00Z"), null),
				Arguments.of(Instant.parse("2021-12-31T00:00:00Z"), null),
				Arguments.of(Instant.parse("2022-01-01T00:00:00Z"), CryptoStats.builder()
						.symbol("XRP")
						.oldestTimestamp(Instant.parse("2022-01-01T00:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-01T21:00:00Z"))
						.minPrice(0.8298)
						.maxPrice(0.8458)
						.build()),
				Arguments.of(Instant.parse("2022-01-02T00:00:00Z"),
						CryptoStats.builder()
								.symbol("ETH")
								.oldestTimestamp(Instant.parse("2022-01-02T02:00:00Z"))
								.newestTimestamp(Instant.parse("2022-01-02T19:00:00Z"))
								.minPrice(3743.17)
								.maxPrice(3823.82)
								.build()
				),
				Arguments.of(Instant.parse("2022-01-03T00:00:00Z"),
						CryptoStats.builder()
								.symbol("ETH")
								.oldestTimestamp(Instant.parse("2022-01-03T00:00:00Z"))
								.newestTimestamp(Instant.parse("2022-01-03T23:00:00Z"))
								.minPrice(3699.02)
								.maxPrice(3828.11)
								.build()),
				Arguments.of(Instant.parse("2022-01-04T00:00:00Z"), CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-04T07:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-04T21:00:00Z"))
						.minPrice(3753.34)
						.maxPrice(3816.37)
						.build()),
				Arguments.of(Instant.parse("2022-01-05T00:00:00Z"), CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-05T00:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-05T23:00:00Z"))
						.minPrice(135.0)
						.maxPrice(147.9)
						.build()),
				Arguments.of(Instant.parse("2022-01-06T00:00:00Z"), CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-06T01:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-06T13:00:00Z"))
						.minPrice(3355.94)
						.maxPrice(3538.85)
						.build()),
				Arguments.of(Instant.parse("2022-01-07T00:00:00Z"), CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-07T00:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-07T21:00:00Z"))
						.minPrice(0.1543)
						.maxPrice(0.16)
						.build()),
				Arguments.of(Instant.parse("2022-01-08T00:00:00Z"), CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-08T11:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-08T19:00:00Z"))
						.minPrice(3009.22)
						.maxPrice(3221.33)
						.build()),
				Arguments.of(Instant.parse("2022-01-09T00:00:00Z"), CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-09T05:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-09T20:00:00Z"))
						.minPrice(3127.51)
						.maxPrice(3204.11)
						.build()),
				Arguments.of(Instant.parse("2022-01-10T00:00:00Z"), CryptoStats.builder()
						.symbol("XRP")
						.oldestTimestamp(Instant.parse("2022-01-10T02:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-10T20:00:00Z"))
						.minPrice(0.7226)
						.maxPrice(0.7507)
						.build()),
				Arguments.of(Instant.parse("2022-01-11T00:00:00Z"), CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-11T01:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-11T06:00:00Z"))
						.minPrice(126.0)
						.maxPrice(128.7)
						.build()),
				Arguments.of(Instant.parse("2022-01-12T00:00:00Z"), CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-12T00:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-12T22:00:00Z"))
						.minPrice(131.3)
						.maxPrice(140.5)
						.build()),
				Arguments.of(Instant.parse("2022-01-13T00:00:00Z"), CryptoStats.builder()
						.symbol("XRP")
						.oldestTimestamp(Instant.parse("2022-01-13T02:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-13T19:00:00Z"))
						.minPrice(0.7686)
						.maxPrice(0.793)
						.build()),
				Arguments.of(Instant.parse("2022-01-14T00:00:00Z"), CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-14T01:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-14T17:00:00Z"))
						.minPrice(137.5)
						.maxPrice(145.2)
						.build()),
				Arguments.of(Instant.parse("2022-01-15T00:00:00Z"), CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-15T00:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-15T23:00:00Z"))
						.minPrice(3307.42)
						.maxPrice(3337.59)
						.build()),
				Arguments.of(Instant.parse("2022-01-16T00:00:00Z"), CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-16T01:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-16T21:00:00Z"))
						.minPrice(0.1763)
						.maxPrice(0.1834)
						.build()),
				Arguments.of(Instant.parse("2022-01-17T00:00:00Z"), CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-17T03:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-17T22:00:00Z"))
						.minPrice(147.3)
						.maxPrice(151.5)
						.build()),
				Arguments.of(Instant.parse("2022-01-18T00:00:00Z"), CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-18T02:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-18T12:00:00Z"))
						.minPrice(0.165)
						.maxPrice(0.171)
						.build()),
				Arguments.of(Instant.parse("2022-01-19T00:00:00Z"), CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-19T07:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-19T22:00:00Z"))
						.minPrice(0.1618)
						.maxPrice(0.1666)
						.build()),
				Arguments.of(Instant.parse("2022-01-20T00:00:00Z"), CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-20T03:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-20T23:00:00Z"))
						.minPrice(133.5)
						.maxPrice(138.6)
						.build()),
				Arguments.of(Instant.parse("2022-01-21T00:00:00Z"), CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-21T01:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-21T13:00:00Z"))
						.minPrice(121.2)
						.maxPrice(130.1)
						.build()),
				Arguments.of(Instant.parse("2022-01-22T00:00:00Z"), CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-22T05:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-22T23:00:00Z"))
						.minPrice(0.129)
						.maxPrice(0.1433)
						.build()),
				Arguments.of(Instant.parse("2022-01-23T00:00:00Z"), CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-23T00:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-23T23:00:00Z"))
						.minPrice(0.1333)
						.maxPrice(0.1417)
						.build()),
				Arguments.of(Instant.parse("2022-01-24T00:00:00Z"), CryptoStats.builder()
						.symbol("BTC")
						.oldestTimestamp(Instant.parse("2022-01-24T01:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-24T22:00:00Z"))
						.minPrice(33276.59)
						.maxPrice(36807.92)
						.build()),
				Arguments.of(Instant.parse("2022-01-25T00:00:00Z"), CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-25T08:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-25T22:00:00Z"))
						.minPrice(0.1342)
						.maxPrice(0.1416)
						.build()),
				Arguments.of(Instant.parse("2022-01-26T00:00:00Z"), CryptoStats.builder()
						.symbol("DOGE")
						.oldestTimestamp(Instant.parse("2022-01-26T01:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-26T17:00:00Z"))
						.minPrice(0.1418)
						.maxPrice(0.1505)
						.build()),
				Arguments.of(Instant.parse("2022-01-27T00:00:00Z"), CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-27T01:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-27T22:00:00Z"))
						.minPrice(2360.45)
						.maxPrice(2484.44)
						.build()),
				Arguments.of(Instant.parse("2022-01-28T00:00:00Z"), CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-28T10:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-28T22:00:00Z"))
						.minPrice(2389.0)
						.maxPrice(2533.99)
						.build()),
				Arguments.of(Instant.parse("2022-01-29T00:00:00Z"), CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-29T07:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-29T22:00:00Z"))
						.minPrice(2542.68)
						.maxPrice(2614.67)
						.build()),
				Arguments.of(Instant.parse("2022-01-30T00:00:00Z"), CryptoStats.builder()
						.symbol("LTC")
						.oldestTimestamp(Instant.parse("2022-01-30T00:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-30T14:00:00Z"))
						.minPrice(110.9)
						.maxPrice(111.9)
						.build()),
				Arguments.of(Instant.parse("2022-01-31T00:00:00Z"), CryptoStats.builder()
						.symbol("ETH")
						.oldestTimestamp(Instant.parse("2022-01-31T08:00:00Z"))
						.newestTimestamp(Instant.parse("2022-01-31T20:00:00Z"))
						.minPrice(2529.07)
						.maxPrice(2672.5)
						.build()),
				Arguments.of(Instant.parse("2022-02-01T00:00:00Z"), null),
				Arguments.of(Instant.parse("2023-01-01T00:00:00Z"), null)
		);
	}

	@Test
	void testIsKnownSymbol() {
		assertThat(cryptoService.isKnownSymbol("ETH")).isTrue();
		assertThat(cryptoService.isKnownSymbol("OTHER")).isFalse();
	}
}