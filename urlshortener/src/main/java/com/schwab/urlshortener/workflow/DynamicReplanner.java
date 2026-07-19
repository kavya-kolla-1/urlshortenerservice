package com.schwab.urlshortener.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DynamicReplanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicReplanner.class);

	public void replan(WorkflowContext context, WorkflowStage failedStage) {
		LOGGER.warn("Dynamic replanning triggered for {}", failedStage);
		context.getContextData().put("REPLAN_REQUIRED", true);
		context.getContextData().put("FAILED_STAGE", failedStage);
	}

}