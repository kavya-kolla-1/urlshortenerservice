package com.schwab.urlshortener.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WorkflowGraphTest {

	@Test
	@DisplayName("Workflow graph should initialize successfully")
	void testWorkflowGraphCreation() {

		WorkflowGraph graph = new WorkflowGraph();

		assertNotNull(graph);
		assertNotNull(graph.getStartNode());

		assertEquals("Requirement", graph.getStartNode().getName());
	}

	@Test
	@DisplayName("Requirement should point to Design")
	void testRequirementNode() {

		WorkflowGraph graph = new WorkflowGraph();

		WorkflowNode requirement = graph.getStartNode();

		assertEquals(1, requirement.getNextNodes().size());
		assertEquals("Design", requirement.getNextNodes().get(0).getName());
	}

	@Test
	@DisplayName("Design should point to Coding")
	void testDesignNode() {

		WorkflowGraph graph = new WorkflowGraph();

		WorkflowNode design = graph.getStartNode().getNextNodes().get(0);

		assertEquals("Coding", design.getNextNodes().get(0).getName());
	}

	@Test
	@DisplayName("Workflow ends at Release")
	void testReleaseNode() {

		WorkflowNode node = new WorkflowGraph().getStartNode();

		while (!node.getNextNodes().isEmpty()) {
			node = node.getNextNodes().get(0);
		}

		assertEquals("Release", node.getName());
	}

}