package com.assignment.urlshortener.orchestrator.agent;

import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.model.AgentResult;
import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class ArchitectureAgent implements EngineeringAgent{
    @Override
    public TaskType getSupportedTaskType() {
        return TaskType.ARCHITECTURE_DESIGN;
    }

    @Override
    public AgentResult execute(EngineeringTask task, ExecutionContext context) {
        String requirements = context.getArtifact("requirements");
        String output = """
                Architecture Design Completed.

                Task: %s

                Proposed Architecture:
                1. Spring Boot REST API layer.
                2. Service layer for business logic.
                3. Spring Data JPA repository layer.
                4. H2 database for persistence.
                5. Click tracking for analytics.
                6. Centralized exception handling.
                7. Unit and integration testing.
                """.formatted(requirements);
        context.addArtifact("architecture", output);
        return new AgentResult(true, output, null);
    }
}
