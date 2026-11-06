package com.assignment.urlshortener.orchestrator.agent;

import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.model.AgentResult;
import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class CodingAgent implements EngineeringAgent{
    @Override
    public TaskType getSupportedTaskType() {
        return TaskType.CODE_GENERATION;
    }

    @Override
    public AgentResult execute(EngineeringTask task, ExecutionContext context) {
        //throw new RuntimeException("Simulated coding agent failure");
        String architecture = context.getArtifact("architecture");
        String output = """
                Code Generation Completed.

                Task: %s

                Implementation Plan:
                1. Create REST API controllers.
                2. Implement business logic in service classes.
                3. Create JPA entities and repositories.
                4. Implement request and response DTOs.
                5. Add input validation.
                6. Implement centralized exception handling.
                """.formatted(architecture);
        context.addArtifact("code", output);

        return new AgentResult(true, output, null);
    }
}
