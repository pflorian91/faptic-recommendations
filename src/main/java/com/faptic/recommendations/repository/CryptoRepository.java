package com.faptic.recommendations.repository;

import com.faptic.recommendations.model.CryptoRecord;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Getter
@Repository
public class CryptoRepository {

	private final Map<String, List<CryptoRecord>> cryptoRecords = new HashMap<>();

	@PostConstruct
	public void init() throws Exception {
		try {
			log.debug("Loading data from CSV files");
			loadCryptoData();
		} catch (Exception e) {
			log.error("Failed to load data from CSV", e);
			throw e;
		}
	}

	private void loadCryptoData() throws Exception {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources("classpath:prices/*.csv");
		Pattern pattern = Pattern.compile("^[A-Z]+_values\\.csv$");

		for (Resource resource : resources) {
			String filename = resource.getFilename();

			if (filename != null && pattern.matcher(filename).find()) {
				String cryptoName = filename.replace("_values.csv", "");
				List<CryptoRecord> records = readCryptoData("prices/" + cryptoName);
				cryptoRecords.put(cryptoName, records);
				log.debug("Read data for {}", cryptoName);
			} else {
				log.warn("Skipped file with unmatched format: {}", filename);
			}
		}
	}


	private List<CryptoRecord> readCryptoData(String path) throws IOException {
		List<CryptoRecord> records = new ArrayList<>();
		String filePath = path + "_values.csv";
		Pattern pattern = Pattern.compile("^\\d+,[A-Z]+,\\d+(\\.\\d+)?$"); // Regex for long,string,double

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource(filePath).getInputStream()))) {
			String line;
			br.readLine(); // Skip the header

			while ((line = br.readLine()) != null) {

				if (pattern.matcher(line).matches()) {
					try {
						String[] values = line.split(",");
						Instant timestamp = Instant.ofEpochMilli(Long.parseLong(values[0].trim()));
						String symbol = values[1].trim();
						double price = Double.parseDouble(values[2].trim());

						records.add(CryptoRecord.builder()
								.timestamp(timestamp)
								.symbol(symbol)
								.price(price)
								.build());

					} catch (NumberFormatException e) {
						log.warn("Invalid number format in line: `{}` in filepath {}. Skipping it..", line, filePath, e);
					}
				} else {
					log.warn("Failed to pattern match line `{}` in filepath {}. Skipping it..", line, filePath);
				}
			}

		} catch (Exception e) {
			log.error("Exception occurred while reading crypto data {}", filePath);
			throw e;
		}

		return records;
	}
}

