package com.celonis.challenge.facade;


import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.request.TaskRequest;
import com.celonis.challenge.response.TaskProgressResponse;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.TaskService;
import com.celonis.challenge.util.TaskConvertUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskFacade {

    private final TaskService taskService;
    private final FileService fileService;

    public TaskFacade(TaskService taskService, FileService fileService) {
        this.taskService = taskService;
        this.fileService = fileService;
    }

    public ResponseEntity<FileSystemResource> getTaskResult(String taskId) {
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
        URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
        if (url == null) {
            throw new InternalException("Zip file not found");
        }
        try {
            ProjectGenerationTask updateProjectGenerationTask = fileService.storeResult(task, url);
            taskService.update(taskId, updateProjectGenerationTask);
        } catch (Exception e) {
            throw new InternalException(e);
        }

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
