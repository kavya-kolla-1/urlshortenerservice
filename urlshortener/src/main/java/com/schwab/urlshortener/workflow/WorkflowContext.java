package com.schwab.urlshortener.workflow;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class WorkflowContext {

	private String workflowId;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private boolean approved;

	private int retryCount;

	private Map<String, Object> contextData = new HashMap<>();

	public WorkflowContext() {
		this.startTime = LocalDateTime.now();
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void incrementRetryCount() {
		this.retryCount++;
	}

	public Map<String, Object> getContextData() {
		return contextData;
	}

}