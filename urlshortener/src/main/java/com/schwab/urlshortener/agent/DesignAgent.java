package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class DesignAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(DesignAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Architecture design started");
		context.getContextData().put("design", "Completed");
		return new AgentResult(true, "DesignAgent", "Design completed");
	}

}