package com.schwab.urlshortener.workflow;

import java.time.LocalDateTime;

public class WorkflowStatus {

	private String workflowId;

	private WorkflowStage currentStage;

	private boolean completed;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	public WorkflowStatus() {
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public WorkflowStage getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(WorkflowStage currentStage) {
		this.currentStage = currentStage;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

}