package com.faptic.recommendations.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI apiInfo() {
		return new OpenAPI().info(new Info().title("Recommendation service").version("1.0.0"));
	}

	@Bean
	public GroupedOpenApi httpApi() {
		return GroupedOpenApi.builder()
				.group("Crypto stats")
				.pathsToMatch("/api/**")
				.build();
	}
}