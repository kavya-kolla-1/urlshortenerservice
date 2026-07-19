package com.schwab.urlshortener.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RollbackManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(RollbackManager.class);

	public void rollback(WorkflowContext context, String failedStage) {
		LOGGER.warn("Rollback initiated.");
		LOGGER.warn("Workflow Id : {}", context.getWorkflowId());
		LOGGER.warn("Failed Stage : {}", failedStage);
		context.getContextData().put("ROLLBACK", true);
	}

}