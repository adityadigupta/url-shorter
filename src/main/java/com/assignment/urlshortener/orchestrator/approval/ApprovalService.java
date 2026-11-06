package com.assignment.urlshortener.orchestrator.approval;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApprovalService {
    private final Map<String, ApprovalRequest> approvals =
            new ConcurrentHashMap<>();

    public ApprovalRequest createApproval(
            String runId,
            String taskId,
            String description) {

        ApprovalRequest request =
                new ApprovalRequest(runId, taskId, description);

        approvals.put(request.getApprovalId(), request);

        return request;
    }

    public ApprovalRequest getApproval(String approvalId) {
        return approvals.get(approvalId);
    }

    public List<ApprovalRequest> getApprovalsByRunId(String runId) {

        return approvals.values()
                .stream()
                .filter(request -> request.getRunId().equals(runId))
                .toList();
    }
    public ApprovalRequest approve(String approvalId) {

        ApprovalRequest request = approvals.get(approvalId);

        if (request == null) {
            throw new IllegalArgumentException(
                    "Approval request not found: " + approvalId
            );
        }

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalStateException(
                    "Approval request has already been processed"
            );
        }

        request.setStatus(ApprovalStatus.APPROVED);

        return request;
    }

    public ApprovalRequest reject(String approvalId) {

        ApprovalRequest request = approvals.get(approvalId);

        if (request == null) {
            throw new IllegalArgumentException(
                    "Approval request not found: " + approvalId
            );
        }

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalStateException(
                    "Approval request has already been processed"
            );
        }

        request.setStatus(ApprovalStatus.REJECTED);

        return request;
    }
}
