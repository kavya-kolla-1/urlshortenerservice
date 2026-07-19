package com.schwab.urlshortener.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

	@Value("${application.base-url}")
	private String baseUrl;

	public String getBaseUrl() {
		return baseUrl;
	}

}