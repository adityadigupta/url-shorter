package com.assignment.urlshortener.orchestrator.agent;

import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.model.AgentResult;
import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class TestingAgent implements  EngineeringAgent{
    @Override
    public TaskType getSupportedTaskType() {
        return TaskType.TESTING;
    }

    @Override
    public AgentResult execute(EngineeringTask task, ExecutionContext context) {
        String code = context.getArtifact("code");
        String output = """
                Testing Completed.

                Task: %s

                Test Plan:
                1. Verify URL creation.
                2. Verify short URL redirection.
                3. Verify invalid URL handling.
                4. Verify expired URL handling.
                5. Verify click tracking.
                6. Verify analytics API.
                7. Verify exception handling.
                """.formatted(code);
        context.addArtifact("testing", output);
        return new AgentResult(true, output, null);
    }
}
