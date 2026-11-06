package com.assignment.urlshortener.orchestrator.engine;


import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskDependencyGraph {
    private final Map<String, EngineeringTask> tasks =
            new LinkedHashMap<>();

    public void addTask(EngineeringTask task) {

        if (tasks.putIfAbsent(task.getTaskId(), task) != null) {
            throw new IllegalArgumentException(
                    "Duplicate task ID: " + task.getTaskId()
            );
        }
    }

    public List<EngineeringTask> getReadyTasks() {

        List<EngineeringTask> readyTasks = new ArrayList<>();
        for (EngineeringTask task : tasks.values()) {
            if (task.getStatus() != TaskStatus.PENDING) {
                continue;
            }
            boolean dependenciesCompleted = true;
            boolean dependencyFailed = false;
            for (String dependencyId : task.getDependencies()) {
                EngineeringTask dependency = tasks.get(dependencyId);
                if (dependency == null) {
                    dependencyFailed = true;
                    break;
                }
                if (dependency.getStatus() == TaskStatus.FAILED
                        || dependency.getStatus() == TaskStatus.BLOCKED
                        || dependency.getStatus() == TaskStatus.ROLLED_BACK) {
                    dependencyFailed = true;
                    break;
                }
                if (dependency.getStatus() != TaskStatus.COMPLETED) {
                    dependenciesCompleted = false;
                }
            }
            if (dependencyFailed) {
                task.setStatus(TaskStatus.BLOCKED);
            } else if (dependenciesCompleted) {
                task.setStatus(TaskStatus.READY);
                readyTasks.add(task);
            }
        }
        return readyTasks;
    }
    public void blockUnfinishedTasks() {

        for (EngineeringTask task : tasks.values()) {

            if (task.getStatus() == TaskStatus.PENDING
                    || task.getStatus() == TaskStatus.READY) {

                task.setStatus(TaskStatus.BLOCKED);
            }
        }
    }

    public List<EngineeringTask> getAllTasks() {
        return List.copyOf(tasks.values());
    }
}
