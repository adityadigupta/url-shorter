package com.assignment.urlshortener.orchestrator.context;


import com.assignment.urlshortener.orchestrator.model.WorkflowStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class ExecutionContext {
    private final String runId;
    private final String originalRequirement;
    private WorkflowStatus status;
    private final Map<String, String> artifacts =
            new ConcurrentHashMap<>();

    public ExecutionContext(String runId, String originalRequirement) {
        this.runId = runId;
        this.originalRequirement = originalRequirement;
        this.status = WorkflowStatus.RUNNING;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public void addArtifact(String key, String value) {
        artifacts.put(key, value);
    }

    public String getArtifact(String key) {
        return artifacts.get(key);
    }

    public Map<String, String> getAllArtifacts() {
        return Map.copyOf(artifacts);
    }

    public String getRunId() {
        return runId;
    }

    public String getOriginalRequirement() {
        return originalRequirement;
    }
}
