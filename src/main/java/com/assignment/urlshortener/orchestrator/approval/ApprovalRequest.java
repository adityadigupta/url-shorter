package com.assignment.urlshortener.orchestrator.approval;

import java.time.LocalDateTime;
import java.util.UUID;

public class ApprovalRequest {
    private String approvalId;
    private String runId;
    private String taskId;
    private String description;
    private ApprovalStatus status;
    private LocalDateTime createdAt;

    public ApprovalRequest(String runId, String taskId,
                           String description) {

        this.approvalId = UUID.randomUUID().toString();
        this.runId = runId;
        this.taskId = taskId;
        this.description = description;
        this.status = ApprovalStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getApprovalId() {
        return approvalId;
    }

    public String getRunId() {
        return runId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
