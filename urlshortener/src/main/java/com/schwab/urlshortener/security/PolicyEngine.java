package com.schwab.urlshortener.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PolicyEngine {

	private static final Logger LOGGER = LoggerFactory.getLogger(PolicyEngine.class);

	public void validate(String stage) {
		LOGGER.info("Policy validation started for {}", stage);
		if (stage == null || stage.isBlank()) {
			throw new IllegalArgumentException("Invalid workflow stage");
		}
		LOGGER.info("Policy validation completed");

	}

}