package com.schwab.urlshortener.workflow;

import java.util.ArrayList;
import java.util.List;

public class WorkflowNode {

	private final String name;

	private final List<WorkflowNode> nextNodes;

	public WorkflowNode(String name) {
		this.name = name;
		this.nextNodes = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public List<WorkflowNode> getNextNodes() {
		return nextNodes;
	}

	public void addNextNode(WorkflowNode node) {
		nextNodes.add(node);
	}

}