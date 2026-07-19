package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class TestAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Executing test phase.");
		context.getContextData().put("testing", "Completed");
		return new AgentResult(true, "TestAgent", "Testing completed successfully");
	}

}