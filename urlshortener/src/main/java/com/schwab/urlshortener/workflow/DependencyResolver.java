package com.schwab.urlshortener.workflow;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DependencyResolver {

	public List<WorkflowStage> getExecutionPlan() {
		List<WorkflowStage> stages = new ArrayList<>();
		stages.add(WorkflowStage.REQUIREMENT);
		stages.add(WorkflowStage.TASK_PLANNING);
		stages.add(WorkflowStage.IMPACT_ANALYSIS);
		stages.add(WorkflowStage.DESIGN);
		stages.add(WorkflowStage.DOCUMENTATION);
		stages.add(WorkflowStage.CODING);
		stages.add(WorkflowStage.TESTING);
		stages.add(WorkflowStage.VALIDATION);
		stages.add(WorkflowStage.APPROVAL);
		stages.add(WorkflowStage.RELEASE);
		stages.add(WorkflowStage.COMPLETED);
		return stages;
	}

}