package com.assignment.urlshortener.orchestrator.model;

public enum TaskStatus {
    PENDING,
    READY,
    RUNNING,
    WAITING_FOR_APPROVAL,
    COMPLETED,
    FAILED,
    BLOCKED,
    SKIPPED,
    ROLLED_BACK

}
