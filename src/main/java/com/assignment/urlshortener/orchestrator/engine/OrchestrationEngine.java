package com.assignment.urlshortener.orchestrator.engine;

import com.assignment.urlshortener.orchestrator.agent.EngineeringAgent;
import com.assignment.urlshortener.orchestrator.approval.ApprovalRequest;
import com.assignment.urlshortener.orchestrator.approval.ApprovalService;
import com.assignment.urlshortener.orchestrator.audit.AuditEvent;
import com.assignment.urlshortener.orchestrator.audit.AuditService;
import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrchestrationEngine {
    @Autowired
    AgentRegistry agentRegistry;
    @Autowired
    AuditService auditService;
    @Autowired
    ApprovalService approvalService;
    public void execute(TaskDependencyGraph dependencyGraph, ExecutionContext context) {

        while (true) {
            List<EngineeringTask> readyTasks = dependencyGraph.getReadyTasks();

            if (readyTasks.isEmpty()) {
                break;
            }

            for (EngineeringTask task : readyTasks) {
                if (context.getStatus() == WorkflowStatus.SAFE_STOPPED) {
                    break;
                }
                if (task.getTaskType() == TaskType.CODE_GENERATION
                        && context.getArtifact("code-generation-approved") == null) {

                    ApprovalRequest approvalRequest = approvalService.createApproval(
                            context.getRunId(),
                            task.getTaskId(),
                            "Human approval required before code generation."
                    );

                    task.setStatus(TaskStatus.WAITING_FOR_APPROVAL);
                    context.setStatus(WorkflowStatus.WAITING_FOR_APPROVAL);

                    auditService.recordEvent(
                            new AuditEvent(
                                    context.getRunId(),
                                    task.getTaskId(),
                                    task.getTaskName(),
                                    "APPROVAL_REQUESTED",
                                    "Approval ID: " + approvalRequest.getApprovalId()
                            )
                    );

                    return;
                }
                task.setStatus(TaskStatus.RUNNING);
                auditService.recordEvent(
                        new AuditEvent(
                                context.getRunId(),
                                task.getTaskId(),
                                task.getTaskName(),
                                "TASK_STARTED",
                                "Task execution started"
                        )
                );
                try {

                    EngineeringAgent agent =
                            agentRegistry.getAgent(task.getTaskType());

                    AgentResult result = agent.execute(task, context);

                    if (result.isSuccess()) {

                            task.setStatus(TaskStatus.COMPLETED);

                            auditService.recordEvent(
                                    new AuditEvent(
                                            context.getRunId(),
                                            task.getTaskId(),
                                            task.getTaskName(),
                                            "TASK_COMPLETED",
                                            "Task execution completed successfully"
                                    )
                            );


                    } else if (task.canRetry()) {

                        task.incrementRetryCount();
                        task.setStatus(TaskStatus.PENDING);
                        auditService.recordEvent(
                                new AuditEvent(
                                        context.getRunId(),
                                        task.getTaskId(),
                                        task.getTaskName(),
                                        "TASK_RETRY",
                                        "Retry attempt: " + task.getRetryCount()
                                )
                        );
                        System.out.println(
                                "Retrying task: " + task.getTaskName()
                                        + ", retry count: " + task.getRetryCount()
                        );

                    } else {

                        task.setStatus(TaskStatus.FAILED);
                        context.setStatus(WorkflowStatus.SAFE_STOPPED);
                        dependencyGraph.blockUnfinishedTasks();
                        auditService.recordEvent(
                                new AuditEvent(
                                        context.getRunId(),
                                        task.getTaskId(),
                                        task.getTaskName(),
                                        "TASK_FAILED",
                                        "Task failed after exhausting all retries"
                                )
                        );
                        System.err.println(
                                "Task failed after retries: " + task.getTaskName()
                        );
                    }

                } catch (Exception exception) {

                    if (task.canRetry()) {

                        task.incrementRetryCount();
                        task.setStatus(TaskStatus.PENDING);
                        auditService.recordEvent(
                                new AuditEvent(
                                        context.getRunId(),
                                        task.getTaskId(),
                                        task.getTaskName(),
                                        "TASK_RETRY",
                                        "Exception occurred. Retry attempt: "
                                                + task.getRetryCount()
                                                + ". Error: " + exception.getMessage()
                                )
                        );
                    } else {

                        task.setStatus(TaskStatus.FAILED);
                        context.setStatus(WorkflowStatus.SAFE_STOPPED);
                        dependencyGraph.blockUnfinishedTasks();

                        auditService.recordEvent(
                                new AuditEvent(
                                        context.getRunId(),
                                        task.getTaskId(),
                                        task.getTaskName(),
                                        "TASK_FAILED",
                                        "Task failed due to exception: "
                                                + exception.getMessage()
                                )
                        );
                        System.err.println(
                                "Task failed: " + task.getTaskName()
                        );
                    }
                }
            }
            if (context.getStatus() == WorkflowStatus.SAFE_STOPPED) {
                break;
            }
        }
            boolean waitingForApproval = dependencyGraph.getAllTasks()
                    .stream()
                    .anyMatch(task -> task.getStatus() == TaskStatus.WAITING_FOR_APPROVAL);

            boolean allTasksCompleted = dependencyGraph.getAllTasks()
                    .stream()
                    .allMatch(task -> task.getStatus() == TaskStatus.COMPLETED);

            if (waitingForApproval) {
                context.setStatus(WorkflowStatus.WAITING_FOR_APPROVAL);
                return;
            }

        if (context.getStatus() == WorkflowStatus.SAFE_STOPPED) {
        } else if (allTasksCompleted) {
            context.setStatus(WorkflowStatus.COMPLETED);
        } else {
            context.setStatus(WorkflowStatus.FAILED);
        }
        auditService.recordEvent(
                new AuditEvent(
                        context.getRunId(),
                        null,
                        "Workflow",
                        allTasksCompleted
                                ? "WORKFLOW_COMPLETED"
                                : "WORKFLOW_FAILED",
                        allTasksCompleted
                                ? "Workflow completed successfully"
                                : "Workflow failed"
                )
        );
    }
}
