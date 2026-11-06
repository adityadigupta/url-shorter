package com.assignment.urlshortener.orchestrator.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgentResult {
    private boolean success;
    private String output;
    private String errorMessage;
    private LocalDateTime executedAt;

    public AgentResult() {
        this.executedAt = LocalDateTime.now();
    }

    public AgentResult(boolean success, String output, String errorMessage) {
        this.success = success;
        this.output = output;
        this.errorMessage = errorMessage;
        this.executedAt = LocalDateTime.now();
    }
}
