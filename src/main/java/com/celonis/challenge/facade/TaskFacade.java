package com.celonis.challenge.facade;


import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.TaskService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URL;
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

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        return taskService.createTask(projectGenerationTask);
    }

    public ProjectGenerationTask getTask(String taskId) {
        return taskService.getTask(taskId);
    }

    public ProjectGenerationTask update(String taskId, ProjectGenerationTask projectGenerationTask) {
        return taskService.update(taskId, projectGenerationTask);
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
}
