package com.assignment.urlshortener.orchestrator.model;

public enum WorkflowStatus {
    RUNNING,
    WAITING_FOR_APPROVAL,
    COMPLETED,
    FAILED,
    SAFE_STOPPED
}
