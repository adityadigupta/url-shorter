package com.assignment.urlshortener.orchestrator.engine;

import com.assignment.urlshortener.orchestrator.model.EngineeringTask;
import com.assignment.urlshortener.orchestrator.model.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkflowPlanner {

    public TaskDependencyGraph createUrlShortenerWorkflow() {
        TaskDependencyGraph dependencyGraph=new TaskDependencyGraph();
        EngineeringTask requirements = createTask(
                "Analyze Requirements",
                TaskType.REQUIREMENTS_ANALYSIS
        );

        EngineeringTask architecture = createTask(
                "Design Architecture",
                TaskType.ARCHITECTURE_DESIGN
        );

        EngineeringTask coding = createTask(
                "Generate Code",
                TaskType.CODE_GENERATION
        );

        EngineeringTask testing = createTask(
                "Execute Tests",
                TaskType.TESTING
        );

        EngineeringTask review = createTask(
                "Review Code",
                TaskType.CODE_REVIEW
        );

        EngineeringTask documentation = createTask(
                "Generate Documentation",
                TaskType.DOCUMENTATION
        );

        architecture.setDependencies(
                List.of(requirements.getTaskId())
        );

        coding.setDependencies(
                List.of(architecture.getTaskId())
        );

        testing.setDependencies(
                List.of(coding.getTaskId())
        );

        review.setDependencies(
                List.of(coding.getTaskId(), testing.getTaskId())
        );

        documentation.setDependencies(
                List.of(
                        requirements.getTaskId(),
                        architecture.getTaskId(),
                        coding.getTaskId(),
                        testing.getTaskId(),
                        review.getTaskId()
                )
        );

        dependencyGraph.addTask(requirements);
        dependencyGraph.addTask(architecture);
        dependencyGraph.addTask(coding);
        dependencyGraph.addTask(testing);
        dependencyGraph.addTask(review);
        dependencyGraph.addTask(documentation);
        return  dependencyGraph;
    }

    private EngineeringTask createTask(
            String taskName,
            TaskType taskType) {

        EngineeringTask task = new EngineeringTask();

        task.setTaskName(taskName);
        task.setTaskType(taskType);

        return task;
    }
}
