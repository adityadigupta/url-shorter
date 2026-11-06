package com.assignment.urlshortener.orchestrator.agent;

import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.model.AgentResult;
import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class DocumentationAgent implements EngineeringAgent {
    @Override
    public TaskType getSupportedTaskType() {
        return TaskType.DOCUMENTATION;
    }

    @Override
    public AgentResult execute(EngineeringTask task, ExecutionContext context) {
        String requirements = context.getArtifact("requirements");
        String architecture = context.getArtifact("architecture");
        String code = context.getArtifact("code");
        String testing = context.getArtifact("testing");
        String review = context.getArtifact("code-review");

        if (requirements == null || architecture == null
                || code == null || testing == null || review == null) {

            return new AgentResult(
                    false,
                    null,
                    "One or more required engineering artifacts are missing."
            );
        }
        String output = """
                Documentation Completed.
                
                1. Requirements:
                %s
                
                2. Architecture:
                %s
                
                3. Implementation:
                %s
                
                4. Testing:
                %s
                
                5. Code Review:
                %s
                
                6. Documentation Checklist:
                - Project architecture overview.
                - API documentation.
                - Setup and installation instructions.
                - Testing instructions.
                - Known limitations and tradeoffs.
                """.formatted(
                requirements,
                architecture,
                code,
                testing,
                review
        );
        context.addArtifact("documentation", output);
        return new AgentResult(true, output, null);
    }
}
