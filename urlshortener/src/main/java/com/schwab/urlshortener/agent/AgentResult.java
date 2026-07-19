package com.schwab.urlshortener.agent;

public class AgentResult {

	private boolean success;

	private String agentName;

	private String message;

	public AgentResult() {
	}

	public AgentResult(boolean success, String agentName, String message) {

		this.success = success;
		this.agentName = agentName;
		this.message = message;

	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}