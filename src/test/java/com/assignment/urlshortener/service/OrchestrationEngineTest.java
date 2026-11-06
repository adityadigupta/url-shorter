package com.assignment.urlshortener.service;

import com.assignment.urlshortener.orchestrator.agent.EngineeringAgent;
import com.assignment.urlshortener.orchestrator.approval.ApprovalService;
import com.assignment.urlshortener.orchestrator.audit.AuditService;
import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.engine.AgentRegistry;
import com.assignment.urlshortener.orchestrator.engine.OrchestrationEngine;
import com.assignment.urlshortener.orchestrator.engine.TaskDependencyGraph;
import com.assignment.urlshortener.orchestrator.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrchestrationEngineTest {
    private final AgentRegistry agentRegistry = mock(AgentRegistry.class);
    private final AuditService auditService = mock(AuditService.class);
    private final ApprovalService approvalService = mock(ApprovalService.class);

    private final OrchestrationEngine orchestrationEngine =
            new OrchestrationEngine();

    private void setUp() {
        ReflectionTestUtils.setField(
                orchestrationEngine, "agentRegistry", agentRegistry);

        ReflectionTestUtils.setField(
                orchestrationEngine, "auditService", auditService);

        ReflectionTestUtils.setField(
                orchestrationEngine, "approvalService", approvalService);
    }

    @Test
    void shouldRetryTaskAndCompleteOnThirdAttempt() {

        setUp();
        EngineeringAgent agent = mock(EngineeringAgent.class);
        EngineeringTask task = new EngineeringTask();
        task.setTaskName("Analyze Requirements");
        task.setTaskType(TaskType.REQUIREMENTS_ANALYSIS);
        TaskDependencyGraph graph = new TaskDependencyGraph();
        graph.addTask(task);
        ExecutionContext context = new ExecutionContext(
                "test-run-123",
                "Build a URL shortener"
        );

        when(agentRegistry.getAgent(TaskType.REQUIREMENTS_ANALYSIS))
                .thenReturn(agent);

        when(agent.execute(task, context))
                .thenReturn(
                        new AgentResult(false, null, "First failure"),
                        new AgentResult(false, null, "Second failure"),
                        new AgentResult(true, "Requirements completed", null)
                );

        orchestrationEngine.execute(graph, context);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(2, task.getRetryCount());
        verify(agent, times(3)).execute(task, context);
    }

    @Test
    void shouldFailTaskAfterExhaustingRetries() {
        setUp();
        EngineeringAgent agent = mock(EngineeringAgent.class);
        EngineeringTask task = new EngineeringTask();
        task.setTaskName("Analyze Requirements");
        task.setTaskType(TaskType.REQUIREMENTS_ANALYSIS);
        TaskDependencyGraph graph = new TaskDependencyGraph();

        EngineeringTask dependentTask = new EngineeringTask();
        EngineeringTask independentTask = new EngineeringTask();
        independentTask.setTaskName("Prepare Documentation");
        independentTask.setTaskType(TaskType.DOCUMENTATION);
        dependentTask.setTaskName("Design Architecture");
        dependentTask.setTaskType(TaskType.ARCHITECTURE_DESIGN);
        dependentTask.getDependencies().add(task.getTaskId());
        graph.addTask(task);
        graph.addTask(dependentTask);
        graph.addTask(independentTask);
        ExecutionContext context = new ExecutionContext(
                "test-run-456",
                "Build a URL shortener"
        );
        when(agentRegistry.getAgent(TaskType.REQUIREMENTS_ANALYSIS))
                .thenReturn(agent);
        when(agent.execute(task, context))
                .thenReturn(
                        new AgentResult(false, null, "Failure 1"),
                        new AgentResult(false, null, "Failure 2"),
                        new AgentResult(false, null, "Failure 3")
                );
        orchestrationEngine.execute(graph, context);
        assertEquals(TaskStatus.FAILED, task.getStatus());
        assertEquals(TaskStatus.BLOCKED, dependentTask.getStatus());
        assertEquals(TaskStatus.BLOCKED, independentTask.getStatus());
        assertEquals(2, task.getRetryCount());
        assertEquals(WorkflowStatus.SAFE_STOPPED, context.getStatus());
        verify(agent, times(3)).execute(task, context);
    }

    @Test
    void shouldRetryWhenAgentThrowsException() {
        setUp();
        EngineeringAgent agent = mock(EngineeringAgent.class);
        EngineeringTask task = new EngineeringTask();
        task.setTaskName("Analyze Requirements");
        task.setTaskType(TaskType.REQUIREMENTS_ANALYSIS);
        TaskDependencyGraph graph = new TaskDependencyGraph();
        graph.addTask(task);
        ExecutionContext context = new ExecutionContext(
                "test-run-789",
                "Build a URL shortener"
        );
        when(agentRegistry.getAgent(TaskType.REQUIREMENTS_ANALYSIS))
                .thenReturn(agent);
        when(agent.execute(task, context))
                .thenThrow(new RuntimeException("Temporary service failure"))
                .thenThrow(new RuntimeException("Temporary service failure"))
                .thenReturn(new AgentResult(true, "Completed", null));
        orchestrationEngine.execute(graph, context);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(2, task.getRetryCount());
        verify(agent, times(3)).execute(task, context);
    }
}
