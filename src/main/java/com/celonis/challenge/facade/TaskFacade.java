package com.celonis.challenge.facade;


import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.request.TaskRequest;
import com.celonis.challenge.response.TaskProgressResponse;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.TaskService;
import com.celonis.challenge.util.TaskConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskFacade {

    private final TaskService taskService;
    private final FileService fileService;
    private final Resource resource;

    public TaskFacade(TaskService taskService, FileService fileService,
                      @Value("classpath:challenge.zip") Resource resource) {
        this.taskService = taskService;
        this.fileService = fileService;
        this.resource = resource;
    }

    public FileSystemResource getTaskResult(String taskId) {
        ProjectGenerationTask projectGenerationTask = taskService.getTask(taskId);
        return fileService.getTaskResult(projectGenerationTask);
    }

    public List<ProjectGenerationTask> listTasks() {
        return taskService.listTasks();
    }

    public ProjectGenerationTask createTask(TaskRequest taskRequest) {
        ProjectGenerationTask task = TaskConvertUtil.convert(taskRequest);
        return taskService.createTask(task);
    }

    public ProjectGenerationTask getTask(String taskId) {
        return taskService.getTask(taskId);
    }

    public ProjectGenerationTask update(String taskId, TaskRequest taskRequest) {
        ProjectGenerationTask task = TaskConvertUtil.convert(taskRequest);
        return taskService.update(taskId, task);
    }

    public void delete(String taskId) {
        taskService.delete(taskId);
    }

    public void executeTask(String taskId) {
        ProjectGenerationTask task = taskService.getTask(taskId);
        task = fileService.storeResult(task, resource);
        task = taskService.update(taskId, task);
        taskService.executeTask(task);
    }

    public TaskProgressResponse getTaskProgress(String taskId) {
        Integer taskProgress = taskService.getTaskProgress(taskId);
        return new TaskProgressResponse(taskProgress);
    }

    public void cancelTask(String taskId) {
        taskService.cancelTask(taskId);
    }

    public void deleteUnExecutedTasksOlderThan(LocalDate thresholdDate) {
        taskService.deleteUnExecutedTaskOlderThan(thresholdDate);
    }
}
