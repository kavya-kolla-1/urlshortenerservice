package com.schwab.urlshortener.workflow;

import java.util.HashMap;
import java.util.Map;

public class WorkflowGraph {

	private final Map<String, WorkflowNode> workflowNodes = new HashMap<>();

	private WorkflowNode startNode;

	public WorkflowGraph() {

		WorkflowNode requirement = new WorkflowNode("Requirement");
		WorkflowNode design = new WorkflowNode("Design");
		WorkflowNode coding = new WorkflowNode("Coding");
		WorkflowNode testing = new WorkflowNode("Testing");
		WorkflowNode validation = new WorkflowNode("Validation");
		WorkflowNode release = new WorkflowNode("Release");

		requirement.addNextNode(design);
		design.addNextNode(coding);
		coding.addNextNode(testing);
		testing.addNextNode(validation);
		validation.addNextNode(release);

		workflowNodes.put(requirement.getName(), requirement);
		workflowNodes.put(design.getName(), design);
		workflowNodes.put(coding.getName(), coding);
		workflowNodes.put(testing.getName(), testing);
		workflowNodes.put(validation.getName(), validation);
		workflowNodes.put(release.getName(), release);

		this.startNode = requirement;
	}

	public WorkflowNode getStartNode() {
		return startNode;
	}

	public WorkflowNode getNode(String name) {
		return workflowNodes.get(name);
	}

}