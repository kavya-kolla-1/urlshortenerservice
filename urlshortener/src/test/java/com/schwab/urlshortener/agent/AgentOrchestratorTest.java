package com.schwab.urlshortener.agent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.schwab.urlshortener.metrics.MetricsCollector;
import com.schwab.urlshortener.security.ApprovalGate;
import com.schwab.urlshortener.security.PolicyEngine;

@ExtendWith(MockitoExtension.class)
class AgentOrchestratorTest {

    @Mock
    private RequirementAgent requirementAgent;

    @Mock
    private DesignAgent designAgent;

    @Mock
    private CodingAgent codingAgent;

    @Mock
    private TestAgent testAgent;

    @Mock
    private ValidationAgent validationAgent;

    @Mock
    private ReleaseAgent releaseAgent;

    @Mock
    private PolicyEngine policyEngine;

    @Mock
    private MetricsCollector metricsCollector;

    @Mock
    private ApprovalGate approvalGate;

    @InjectMocks
    private AgentOrchestrator orchestrator;

    @Test
    @DisplayName("Workflow executes successfully")
    void testWorkflowExecution() {
        doNothing().when(policyEngine).validate(anyString());
        when(approvalGate.approve(true)).thenReturn(true);
        when(requirementAgent.execute(any())).thenReturn(new AgentResult(true, "Requirement", "Success"));
        when(designAgent.execute(any())).thenReturn(new AgentResult(true, "Design", "Success"));
        when(codingAgent.execute(any())).thenReturn(new AgentResult(true, "Coding", "Success"));
        when(testAgent.execute(any())).thenReturn(new AgentResult(true, "Testing", "Success"));
        when(validationAgent.execute(any())).thenReturn(new AgentResult(true, "Validation", "Success"));
        when(releaseAgent.execute(any())).thenReturn(new AgentResult(true, "Release", "Success"));

        assertDoesNotThrow(() -> orchestrator.executeWorkflow(true));
        verify(metricsCollector, atLeastOnce()).incrementSuccess();
    }

    @Test
    @DisplayName("Requirement agent invoked")
    void testRequirementAgentCalled() {
        doNothing().when(policyEngine).validate(anyString());
        when(approvalGate.approve(true)).thenReturn(true);
        when(requirementAgent.execute(any())).thenReturn(new AgentResult(true, "Requirement", "Success"));
        when(designAgent.execute(any())).thenReturn(new AgentResult(true, "Design", "Success"));
        when(codingAgent.execute(any())).thenReturn(new AgentResult(true, "Coding", "Success"));
        when(testAgent.execute(any())).thenReturn(new AgentResult(true, "Testing", "Success"));
        when(validationAgent.execute(any())).thenReturn(new AgentResult(true, "Validation", "Success"));
        when(releaseAgent.execute(any())).thenReturn(new AgentResult(true, "Release", "Success"));

        orchestrator.executeWorkflow(true);

        verify(requirementAgent).execute(any());
    }

    @Test
    @DisplayName("All agents invoked in workflow")
    void testAllAgentsInvoked() {
        doNothing().when(policyEngine).validate(anyString());
        when(approvalGate.approve(true)).thenReturn(true);
        when(requirementAgent.execute(any())).thenReturn(new AgentResult(true, "Requirement", "Success"));
        when(designAgent.execute(any())).thenReturn(new AgentResult(true, "Design", "Success"));
        when(codingAgent.execute(any())).thenReturn(new AgentResult(true, "Coding", "Success"));
        when(testAgent.execute(any())).thenReturn(new AgentResult(true, "Testing", "Success"));
        when(validationAgent.execute(any())).thenReturn(new AgentResult(true, "Validation", "Success"));
        when(releaseAgent.execute(any())).thenReturn(new AgentResult(true, "Release", "Success"));

        orchestrator.executeWorkflow(true);

        verify(requirementAgent).execute(any());
        verify(designAgent).execute(any());
        verify(codingAgent).execute(any());
        verify(testAgent).execute(any());
        verify(validationAgent).execute(any());
        verify(releaseAgent).execute(any());
    }

    @Test
    @DisplayName("Workflow failure throws exception with correct message")
    void testWorkflowFailure() {
        doThrow(new RuntimeException("Failure")).when(requirementAgent).execute(any());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orchestrator.executeWorkflow(true));

        assertEquals("Execution Failed", exception.getMessage());
    }

    @Test
    @DisplayName("Failure counter incremented on workflow failure")
    void testFailureMetricIncremented() {
        doThrow(new RuntimeException("Failure")).when(requirementAgent).execute(any());

        assertThrows(RuntimeException.class, () -> orchestrator.executeWorkflow(true));

        verify(metricsCollector, atLeastOnce()).incrementFailure();
    }

    @Test
    @DisplayName("Policy engine is validated before workflow runs")
    void testPolicyEngineValidated() {
        doNothing().when(policyEngine).validate(anyString());
        when(approvalGate.approve(true)).thenReturn(true);
        when(requirementAgent.execute(any())).thenReturn(new AgentResult(true, "Requirement", "Success"));
        when(designAgent.execute(any())).thenReturn(new AgentResult(true, "Design", "Success"));
        when(codingAgent.execute(any())).thenReturn(new AgentResult(true, "Coding", "Success"));
        when(testAgent.execute(any())).thenReturn(new AgentResult(true, "Testing", "Success"));
        when(validationAgent.execute(any())).thenReturn(new AgentResult(true, "Validation", "Success"));
        when(releaseAgent.execute(any())).thenReturn(new AgentResult(true, "Release", "Success"));

        orchestrator.executeWorkflow(true);

        verify(policyEngine, atLeastOnce()).validate(anyString());
    }

    @Test
    @DisplayName("Success counter incremented on successful workflow")
    void testSuccessMetricIncremented() {
        doNothing().when(policyEngine).validate(anyString());
        when(approvalGate.approve(true)).thenReturn(true);
        when(requirementAgent.execute(any())).thenReturn(new AgentResult(true, "Requirement", "Success"));
        when(designAgent.execute(any())).thenReturn(new AgentResult(true, "Design", "Success"));
        when(codingAgent.execute(any())).thenReturn(new AgentResult(true, "Coding", "Success"));
        when(testAgent.execute(any())).thenReturn(new AgentResult(true, "Testing", "Success"));
        when(validationAgent.execute(any())).thenReturn(new AgentResult(true, "Validation", "Success"));
        when(releaseAgent.execute(any())).thenReturn(new AgentResult(true, "Release", "Success"));

        orchestrator.executeWorkflow(true);

        verify(metricsCollector, atLeastOnce()).incrementSuccess();
    }
}
