package com.assignment.urlshortener.service;

import com.assignment.urlshortener.orchestrator.engine.TaskDependencyGraph;
import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskStatus;
import com.assignment.urlshortener.orchestrator.model.TaskType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskDependencyGraphTest {
    @Test
    void shouldBlockDependentTaskWhenDependencyFails() {
        TaskDependencyGraph graph = new TaskDependencyGraph();
        EngineeringTask firstTask = new EngineeringTask();
        firstTask.setTaskName("Generate Code");
        firstTask.setTaskType(TaskType.CODE_GENERATION);
        firstTask.setStatus(TaskStatus.FAILED);
        EngineeringTask secondTask = new EngineeringTask();
        secondTask.setTaskName("Execute Tests");
        secondTask.setTaskType(TaskType.TESTING);
        secondTask.getDependencies().add(firstTask.getTaskId());
        graph.addTask(firstTask);
        graph.addTask(secondTask);
        graph.getReadyTasks();
        assertEquals(TaskStatus.FAILED, firstTask.getStatus());
        assertEquals(TaskStatus.BLOCKED, secondTask.getStatus());
    }
    @Test
    void shouldBlockAllDownstreamTasksWhenDependencyFails() {
        TaskDependencyGraph graph = new TaskDependencyGraph();
        EngineeringTask codeTask = new EngineeringTask();
        codeTask.setStatus(TaskStatus.FAILED);
        EngineeringTask testTask = new EngineeringTask();
        testTask.getDependencies().add(codeTask.getTaskId());
        EngineeringTask reviewTask = new EngineeringTask();
        reviewTask.getDependencies().add(testTask.getTaskId());
        graph.addTask(codeTask);
        graph.addTask(testTask);
        graph.addTask(reviewTask);
        graph.getReadyTasks();
        assertEquals(TaskStatus.BLOCKED, testTask.getStatus());
        assertEquals(TaskStatus.BLOCKED, reviewTask.getStatus());
    }
}
