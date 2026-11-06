package com.assignment.urlshortener.orchestrator.audit;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AuditService {
    private final Map<String, List<AuditEvent>> auditEvents =
            new ConcurrentHashMap<>();

    public void recordEvent(AuditEvent event) {

        auditEvents.computeIfAbsent(
                event.getRunId(),
                key -> new CopyOnWriteArrayList<>()
        ).add(event);
    }

    public List<AuditEvent> getEvents(String runId) {

        return auditEvents.getOrDefault(runId, List.of());
    }
}
