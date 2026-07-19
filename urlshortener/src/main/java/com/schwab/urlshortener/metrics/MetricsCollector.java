package com.schwab.urlshortener.metrics;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MetricsCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricsCollector.class);

	private final AtomicInteger success = new AtomicInteger();

	private final AtomicInteger failure = new AtomicInteger();

	private final AtomicInteger retry = new AtomicInteger();

	private final AtomicInteger rollback = new AtomicInteger();

	public void incrementSuccess() {
		success.incrementAndGet();
	}

	public void incrementFailure() {
		failure.incrementAndGet();
	}

	public void incrementRetry() {
		retry.incrementAndGet();
	}

	public void incrementRollback() {
		rollback.incrementAndGet();
	}

	public void printMetrics() {
		LOGGER.info("Success={}", success.get());
		LOGGER.info("Failure={}", failure.get());
		LOGGER.info("Retry={}", retry.get());
		LOGGER.info("Rollback={}", rollback.get());
	}

}