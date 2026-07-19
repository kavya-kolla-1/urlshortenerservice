package com.schwab.urlshortener.agent;

import com.schwab.urlshortener.workflow.WorkflowContext;

public interface Agent {

	AgentResult execute(WorkflowContext context);

}