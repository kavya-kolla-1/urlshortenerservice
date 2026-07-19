package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class ValidationAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Executing validation phase.");
		context.getContextData().put("validation", "Completed");
		return new AgentResult(true, "ValidationAgent", "Validation completed successfully");
	}
	
}