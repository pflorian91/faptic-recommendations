package com.faptic.recommendations.repository;

import com.faptic.recommendations.model.CryptoRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CryptoRepositoryTest {

	@Autowired
	private CryptoRepository cryptoRepository;

	@Test
	public void testLoadCryptoData() {
		Map<String, List<CryptoRecord>> cryptoRecords = cryptoRepository.getCryptoRecords();

		assertNotNull(cryptoRecords);
		assertThat(cryptoRecords).containsKeys("BTC", "DOGE", "ETH", "LTC", "XRP");

		assertThat(cryptoRecords.get("BTC")).isNotNull().hasSize(100);
		assertThat(cryptoRecords.get("DOGE")).isNotNull().hasSize(90);
		assertThat(cryptoRecords.get("ETH")).isNotNull().hasSize(95);
		assertThat(cryptoRecords.get("LTC")).isNotNull().hasSize(85);
		assertThat(cryptoRecords.get("XRP")).isNotNull().hasSize(80);

		// cherry-pick some values
		assertThat(cryptoRecords.get("BTC")).contains(CryptoRecord.builder()
						.timestamp(Instant.ofEpochMilli(Long.parseLong("1641376800000")))
						.symbol("BTC")
						.price(Double.parseDouble("46858.93"))
				.build());

		assertThat(cryptoRecords.get("DOGE")).contains(CryptoRecord.builder()
				.timestamp(Instant.ofEpochMilli(Long.parseLong("1642629600000")))
				.symbol("DOGE")
				.price(Double.parseDouble("0.1618"))
				.build());

		assertThat(cryptoRecords.get("ETH")).contains(CryptoRecord.builder()
				.timestamp(Instant.ofEpochMilli(Long.parseLong("1643364000000")))
				.symbol("ETH")
				.price(Double.parseDouble("2389"))
				.build());

		assertThat(cryptoRecords.get("LTC")).contains(CryptoRecord.builder()
				.timestamp(Instant.ofEpochMilli(Long.parseLong("1641834000000")))
				.symbol("LTC")
				.price(Double.parseDouble("127.7"))
				.build());

		assertThat(cryptoRecords.get("XRP")).contains(CryptoRecord.builder()
				.timestamp(Instant.ofEpochMilli(Long.parseLong("1642075200000")))
				.symbol("XRP")
				.price(Double.parseDouble("0.7837"))
				.build());
	}
}
