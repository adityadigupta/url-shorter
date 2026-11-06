package com.assignment.urlshortener.orchestrator.approval;

import com.assignment.urlshortener.orchestrator.engine.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {
    @Autowired
    ApprovalService approvalService;
    @Autowired
    OrchestrationService orchestrationService;

    @GetMapping("/{approvalId}")
    public ResponseEntity<ApprovalRequest> getApproval(
            @PathVariable String approvalId) {

        ApprovalRequest request =
                approvalService.getApproval(approvalId);

        if (request == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(request);
    }

    @GetMapping("/run/{runId}")
    public ResponseEntity<List<ApprovalRequest>> getApprovalsByRunId(
            @PathVariable String runId) {

        return ResponseEntity.ok(
                approvalService.getApprovalsByRunId(runId)
        );
    }

    @PostMapping("/{approvalId}/approve")
    public ResponseEntity<ApprovalRequest> approve(
            @PathVariable String approvalId) {
        ApprovalRequest request = approvalService.approve(approvalId);
        orchestrationService.resumeWorkflow(
                request.getRunId(),
                request.getTaskId()
        );
        return ResponseEntity.ok(request);
    }

    @PostMapping("/{approvalId}/reject")
    public ResponseEntity<ApprovalRequest> reject(
            @PathVariable String approvalId) {

        ApprovalRequest request = approvalService.reject(approvalId);

        orchestrationService.rejectWorkflow(
                request.getRunId(),
                request.getTaskId()
        );

        return ResponseEntity.ok(request);
    }
}
