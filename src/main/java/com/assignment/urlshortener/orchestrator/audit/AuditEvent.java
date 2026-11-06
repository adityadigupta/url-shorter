package com.assignment.urlshortener.orchestrator.audit;

import java.time.LocalDateTime;

public class AuditEvent {
    private String runId;
    private String taskId;
    private String taskName;
    private String event;
    private String details;
    private LocalDateTime timestamp;

    public AuditEvent(String runId, String taskId,
                      String taskName, String event,
                      String details) {
        this.runId = runId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.event = event;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public String getRunId() {
        return runId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getEvent() {
        return event;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
