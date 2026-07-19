package com.schwab.urlshortener.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.schwab.urlshortener.workflow.WorkflowContext;

@Component
public class TaskPlannerAgent implements Agent {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskPlannerAgent.class);

	@Override
	public AgentResult execute(WorkflowContext context) {
		LOGGER.info("Creating execution plan.");
		context.getContextData().put("TASK_PLAN", "CREATED");
		return new AgentResult(true, "TaskPlannerAgent", "Execution plan generated");
	}

}