package com.schwab.urlshortener.audit;

import java.time.LocalDateTime;

public class AuditEvent {

	private String workflowId;
	private String stage;
	private String status;
	private String message;
	private LocalDateTime timestamp;

	public AuditEvent() {
		this.timestamp = LocalDateTime.now();
	}

	public AuditEvent(String workflowId, String stage, String status, String message) {

		this.workflowId = workflowId;
		this.stage = stage;
		this.status = status;
		this.message = message;
		this.timestamp = LocalDateTime.now();

	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
}