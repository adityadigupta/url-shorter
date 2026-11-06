package com.assignment.urlshortener.orchestrator.agent;

import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.model.AgentResult;
import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskType;

public interface EngineeringAgent {

    TaskType getSupportedTaskType();

    AgentResult execute(EngineeringTask task, ExecutionContext context);
}
