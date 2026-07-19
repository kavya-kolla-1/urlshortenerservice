package com.schwab.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schwab.urlshortener.common.ApiResponse;
import com.schwab.urlshortener.workflow.WorkflowStatus;

@RestController
@RequestMapping("/api/v1/workflow")
public class WorkflowDashboardController {

	@GetMapping("/{workflowId}")
	public ResponseEntity<ApiResponse<WorkflowStatus>> getWorkflowStatus(@PathVariable String workflowId) {
		WorkflowStatus status = new WorkflowStatus();
		status.setWorkflowId(workflowId);
		return ResponseEntity.ok(new ApiResponse<>(true, "Workflow Status", status));
	}

}