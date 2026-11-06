package com.assignment.urlshortener.orchestrator.agent;

import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.model.AgentResult;
import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class RequirementsAgent implements EngineeringAgent {
    @Override
    public TaskType getSupportedTaskType() {
        return TaskType.REQUIREMENTS_ANALYSIS;
    }

    @Override
    public AgentResult execute(EngineeringTask task, ExecutionContext context) {
        String originalRequirement = context.getOriginalRequirement();
        String output = """
                Requirements Analysis Completed.
                
                Task: %s
                
                Requirements:
                1. Create short URLs.
                2. Redirect users to original URLs.
                3. Track URL clicks.
                4. Provide click analytics.
                5. Handle expired and inactive URLs.
                """.formatted(originalRequirement);
        context.addArtifact("requirements", output);
        return new AgentResult(true, output, null);
    }
}
