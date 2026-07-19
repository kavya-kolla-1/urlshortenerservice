package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class RequirementAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequirementAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Requirement analysis started");
		context.getContextData().put("requirement", "Completed");
		return new AgentResult(true, "RequirementAgent", "Requirement completed");
	}

}