package com.task.challenge.services;

import com.task.challenge.model.ProjectGenerationTask;

import java.time.LocalDate;
import java.util.List;

public interface TaskService {

    List<ProjectGenerationTask> listTasks();

    ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask);

    ProjectGenerationTask getTask(String taskId);

    ProjectGenerationTask update(String taskId, ProjectGenerationTask projectGenerationTask);

    void delete(String taskId);

    Integer getTaskProgress(String taskId);

    void cancelTask(String taskId);

    void deleteUnExecutedTaskOlderThan(LocalDate thresholdDate);

    void executeTask(ProjectGenerationTask task);
}
