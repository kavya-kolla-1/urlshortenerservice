package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class DocumentationAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Generating documentation.");
		context.getContextData().put("DOCUMENTATION", "READY");
		return new AgentResult(true, "DocumentationAgent", "Documentation generated");
	}

}