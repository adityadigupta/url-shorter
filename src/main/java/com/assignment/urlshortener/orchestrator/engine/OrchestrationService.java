package com.assignment.urlshortener.orchestrator.engine;

import com.assignment.urlshortener.orchestrator.approval.ApprovalService;
import com.assignment.urlshortener.orchestrator.audit.AuditEvent;
import com.assignment.urlshortener.orchestrator.audit.AuditService;
import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskStatus;
import com.assignment.urlshortener.orchestrator.model.WorkflowStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrchestrationService {
    @Autowired
    WorkflowPlanner workflowPlanner;
    @Autowired
    OrchestrationEngine orchestrationEngine;
    @Autowired
    private AuditService auditService;
    private final Map<String, List<EngineeringTask>> taskStatuses =
            new ConcurrentHashMap<>();
    private final Map<String, ExecutionContext> executionContexts =
            new ConcurrentHashMap<>();
    private final Map<String, TaskDependencyGraph> workflowGraphs =
            new ConcurrentHashMap<>();
    @Autowired
    ApprovalService approvalService;

    public ExecutionContext startWorkflow(String requirement) {

        String runId = UUID.randomUUID().toString();

        ExecutionContext context =
                new ExecutionContext(runId, requirement);

        TaskDependencyGraph dependencyGraph =
                workflowPlanner.createUrlShortenerWorkflow();
        workflowGraphs.put(runId, dependencyGraph);
        orchestrationEngine.execute(dependencyGraph, context);
        executionContexts.put(runId, context);
        taskStatuses.put(runId, dependencyGraph.getAllTasks());

        return context;
    }

    public List<EngineeringTask> getTaskStatuses(String runId) {
        return taskStatuses.getOrDefault(runId, List.of());
    }

    public TaskDependencyGraph getWorkflowGraph(String runId) {
        return workflowGraphs.get(runId);
    }

    public ExecutionContext getExecutionContext(String runId) {
        return executionContexts.get(runId);
    }

    public void resumeWorkflow(String runId, String taskId) {

        ExecutionContext context = executionContexts.get(runId);
        TaskDependencyGraph graph = workflowGraphs.get(runId);

        if (context == null || graph == null) {
            throw new IllegalArgumentException("Workflow not found: " + runId);
        }

        EngineeringTask task = graph.getAllTasks()
                .stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Task not found: " + taskId));

        context.addArtifact("code-generation-approved", "true");
        auditService.recordEvent(
                new AuditEvent(
                        runId,
                        taskId,
                        task.getTaskName(),
                        "APPROVAL_APPROVED",
                        "Human reviewer approved code generation."
                )
        );
        task.setStatus(TaskStatus.PENDING);
        context.setStatus(WorkflowStatus.FAILED.RUNNING);

        orchestrationEngine.execute(graph, context);

        taskStatuses.put(runId, graph.getAllTasks());
    }

    public void rejectWorkflow(String runId, String taskId) {

        ExecutionContext context = executionContexts.get(runId);
        TaskDependencyGraph graph = workflowGraphs.get(runId);

        if (context == null || graph == null) {
            throw new IllegalArgumentException("Workflow not found: " + runId);
        }

        EngineeringTask task = graph.getAllTasks()
                .stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Task not found: " + taskId));

        task.setStatus(TaskStatus.FAILED);
        context.setStatus(WorkflowStatus.FAILED);
        graph.getReadyTasks();
        auditService.recordEvent(
                new AuditEvent(
                        runId,
                        taskId,
                        task.getTaskName(),
                        "APPROVAL_REJECTED",
                        "Human reviewer rejected code generation."
                )
        );
        taskStatuses.put(runId, graph.getAllTasks());
    }

}