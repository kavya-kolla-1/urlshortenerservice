package com.schwab.urlshortener.constants;

public final class ApplicationConstants {

	private ApplicationConstants() {
	}

	public static final String BASE_URL = "http://localhost:8080/api/";

	public static final String APPLICATION_JSON = "application/json";

	public static final String REQUEST_ID = "REQUEST_ID";

	public static final int MAX_RETRY_COUNT = 3;

	public static final int DEFAULT_CLICK_COUNT = 0;

	public static final String APPROVED = "APPROVED";

	public static final String REJECTED = "REJECTED";

	public static final String WORKFLOW_COMPLETED = "Workflow completed successfully";

	public static final String WORKFLOW_FAILED = "Workflow execution failed";

}