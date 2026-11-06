package com.assignment.urlshortener.orchestrator.controller;

import com.assignment.urlshortener.orchestrator.audit.AuditEvent;
import com.assignment.urlshortener.orchestrator.audit.AuditService;
import com.assignment.urlshortener.orchestrator.context.ExecutionContext;
import com.assignment.urlshortener.orchestrator.engine.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orchestrations")
public class OrchestrationController {
    @Autowired
    OrchestrationService orchestrationService;
    @Autowired
    AuditService auditService;


    @PostMapping
    public ResponseEntity<Map<String, Object>> startWorkflow(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        ExecutionContext context = orchestrationService.startWorkflow(requirement);
        Map<String, Object> response = Map.of(
                "runId", context.getRunId(),
                "status", context.getStatus().name(),
                "artifacts", context.getAllArtifacts(),
                "tasks", orchestrationService.getTaskStatuses(context.getRunId())
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{runId}")
    public ResponseEntity<Map<String, Object>> getWorkflowStatus(@PathVariable String runId) {
        ExecutionContext context = orchestrationService.getExecutionContext(runId);
        if (context == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> response = Map.of(
                "runId", context.getRunId(),
                "status", context.getStatus().name(),
                "artifacts", context.getAllArtifacts(),
                "tasks", orchestrationService.getTaskStatuses(runId)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{runId}/audit")
    public ResponseEntity<List<AuditEvent>> getAuditEvents(@PathVariable String runId) {
        ExecutionContext context = orchestrationService.getExecutionContext(runId);
        if (context == null) {
            return ResponseEntity.notFound().build();
        }
        List<AuditEvent> events = auditService.getEvents(runId);
        return ResponseEntity.ok(events);
    }
}
