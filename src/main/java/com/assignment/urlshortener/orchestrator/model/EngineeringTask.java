package com.assignment.urlshortener.orchestrator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EngineeringTask {
    private String taskId;
    private String taskName;
    private String description;

    private TaskType taskType;
    private TaskStatus status;

    private List<String> dependencies = new ArrayList<>();

    private int retryCount;
    private int maxRetries;

    public EngineeringTask() {
        this.taskId = UUID.randomUUID().toString();
        this.status = TaskStatus.PENDING;
        this.retryCount = 0;
        this.maxRetries = 2;
    }
    public boolean canRetry() {
        return retryCount < maxRetries;
    }
    public void incrementRetryCount() {
        this.retryCount++;
    }

}
