package com.schwab.urlshortener.agent;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.constants.ApplicationConstants;
import com.schwab.urlshortener.metrics.MetricsCollector;
import com.schwab.urlshortener.security.ApprovalGate;
import com.schwab.urlshortener.security.PolicyEngine;
import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class AgentOrchestrator {

	private static final Logger LOGGER = LoggerFactory.getLogger(AgentOrchestrator.class);

	private final List<Agent> agents;

	private final PolicyEngine policyEngine;

	private final MetricsCollector metricsCollector;

	private final ApprovalGate approvalGate;

	public AgentOrchestrator(RequirementAgent requirementAgent, DesignAgent designAgent, CodingAgent codingAgent,
			TestAgent testAgent, ValidationAgent validationAgent, ReleaseAgent releaseAgent, PolicyEngine policyEngine,
			MetricsCollector metricsCollector, ApprovalGate approvalGate) {
		this.agents = Arrays.asList(requirementAgent, designAgent, codingAgent, testAgent, validationAgent,
				releaseAgent);
		this.policyEngine = policyEngine;
		this.metricsCollector = metricsCollector;
		this.approvalGate = approvalGate;
	}

	public void executeWorkflow(boolean approved) {
		WorkflowContext context = new WorkflowContext();
		context.setWorkflowId(java.util.UUID.randomUUID().toString());
		LOGGER.info("Workflow Started {}", context.getWorkflowId());
		try {
			for (Agent agent : agents) {
				policyEngine.validate(agent.getClass().getSimpleName());
				AgentResult result = executeWithRetry(agent, context);
				if (!result.isSuccess()) {
					metricsCollector.incrementFailure();
					throw new RuntimeException(result.getMessage());
				}
				metricsCollector.incrementSuccess();
			}
			if (!approvalGate.approve(approved)) {
				throw new RuntimeException("Deployment approval rejected");
			}
			context.setEndTime(LocalDateTime.now());
			LOGGER.info(ApplicationConstants.WORKFLOW_COMPLETED);
			metricsCollector.printMetrics();
		} catch (Exception ex) {
		    metricsCollector.incrementFailure();
		    LOGGER.error("Workflow Failed : {}", ex.getMessage());
		    throw ex;
		}
	}

	private AgentResult executeWithRetry(Agent agent, WorkflowContext context) {
		int retry = 0;
		while (retry < ApplicationConstants.MAX_RETRY_COUNT) {
			try {
				return agent.execute(context);
			} catch (Exception ex) {
				retry++;
				context.incrementRetryCount();
				metricsCollector.incrementRetry();
				LOGGER.warn("Retry {}", retry);
			}
		}
		return new AgentResult(false, agent.getClass().getSimpleName(), "Execution Failed");
	}

}