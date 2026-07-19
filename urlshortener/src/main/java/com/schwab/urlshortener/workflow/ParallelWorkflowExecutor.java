package com.schwab.urlshortener.workflow;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ParallelWorkflowExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParallelWorkflowExecutor.class);

	public void execute(Runnable designTask, Runnable documentationTask) {
		CompletableFuture<Void> designFuture = CompletableFuture.runAsync(designTask);
		CompletableFuture<Void> documentationFuture = CompletableFuture.runAsync(documentationTask);
		CompletableFuture.allOf(designFuture, documentationFuture).join();
		LOGGER.info("Parallel execution completed.");
	}

}