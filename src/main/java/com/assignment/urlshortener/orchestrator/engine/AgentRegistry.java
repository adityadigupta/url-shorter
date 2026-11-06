package com.assignment.urlshortener.orchestrator.engine;

import com.assignment.urlshortener.orchestrator.agent.EngineeringAgent;
import com.assignment.urlshortener.orchestrator.model.TaskType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class AgentRegistry {
    private final Map<TaskType, EngineeringAgent> agents;
    public AgentRegistry(List<EngineeringAgent> agentList) {

        Map<TaskType, EngineeringAgent> agentMap =
                new EnumMap<>(TaskType.class);

        for (EngineeringAgent agent : agentList) {

            TaskType taskType = agent.getSupportedTaskType();

            if (agentMap.putIfAbsent(taskType, agent) != null) {
                throw new IllegalStateException(
                        "Multiple agents registered for task type: " + taskType
                );
            }
        }

        this.agents = Map.copyOf(agentMap);
    }
    public EngineeringAgent getAgent(TaskType taskType) {

        EngineeringAgent agent = agents.get(taskType);

        if (agent == null) {
            throw new IllegalArgumentException(
                    "No agent registered for task type: " + taskType
            );
        }

        return agent;
    }
}
