package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class ReleaseAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Preparing release.");
		context.getContextData().put("release", "Completed");
		return new AgentResult(true, "ReleaseAgent", "Release completed successfully");
	}
	
}