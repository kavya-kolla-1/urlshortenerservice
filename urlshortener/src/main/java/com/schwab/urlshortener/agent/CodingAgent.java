package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class CodingAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(CodingAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Code generation started");
		context.getContextData().put("coding", "Completed");
		return new AgentResult(true, "CodingAgent", "Coding completed");
	}

}