package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class ImpactAnalysisAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImpactAnalysisAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Performing impact analysis.");
		context.getContextData().put("IMPACT_ANALYSIS", "COMPLETED");
		return new AgentResult(true, "ImpactAnalysisAgent", "Impact analysis completed");
	}

}