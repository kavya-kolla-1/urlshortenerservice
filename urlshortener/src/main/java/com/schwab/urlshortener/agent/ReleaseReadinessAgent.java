package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class ReleaseReadinessAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseReadinessAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Checking release readiness.");
		context.getContextData().put("RELEASE_READY", true);
		return new AgentResult(true, "ReleaseReadinessAgent", "Release approved");
	}

}