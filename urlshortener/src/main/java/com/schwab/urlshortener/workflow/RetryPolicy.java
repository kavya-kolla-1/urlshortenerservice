package com.schwab.urlshortener.workflow;

public class RetryPolicy {

	private int maxRetry = 3;

	private long retryDelay = 1000;

	public int getMaxRetry() {
		return maxRetry;
	}

	public long getRetryDelay() {
		return retryDelay;
	}

}