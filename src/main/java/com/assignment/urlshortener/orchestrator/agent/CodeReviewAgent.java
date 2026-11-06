package com.assignment.urlshortener.orchestrator.agent;

import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.model.AgentResult;
import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class CodeReviewAgent implements  EngineeringAgent{
    @Override
    public TaskType getSupportedTaskType() {
        return TaskType.CODE_REVIEW;
    }

    @Override
    public AgentResult execute(EngineeringTask task, ExecutionContext context) {
        String code = context.getArtifact("code");
        String testing = context.getArtifact("testing");
        String output = """
                Code Review Completed.

                Task: %s

                Review Checklist:
                1. Check code readability and maintainability.
                2. Verify exception handling.
                3. Check input validation.
                4. Review database interactions.
                5. Check security considerations.
                6. Verify test coverage.
                """.formatted(code, testing);
        context.addArtifact("code-review", output);
        return new AgentResult(true, output, null);
    }
}
