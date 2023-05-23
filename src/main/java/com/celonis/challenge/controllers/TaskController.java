package com.celonis.challenge.controllers;

import com.celonis.challenge.facade.TaskFacade;
import com.celonis.challenge.model.ProjectGenerationTask;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@RestController
@RequestMapping(value = "/api/tasks/", produces = APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskFacade taskFacade;

    public TaskController(TaskFacade taskFacade) {
        this.taskFacade = taskFacade;
    }

    @GetMapping
    public List<ProjectGenerationTask> listTasks() {
        return taskFacade.listTasks();
    }

    @PostMapping
    public ProjectGenerationTask createTask(@RequestBody @Valid ProjectGenerationTask projectGenerationTask) {
        return taskFacade.createTask(projectGenerationTask);
    }

    @GetMapping("{taskId}")
    public ProjectGenerationTask getTask(@PathVariable String taskId) {
        return taskFacade.getTask(taskId);
    }

    @PutMapping("{taskId}")
    public ProjectGenerationTask updateTask(@PathVariable String taskId,
                                            @RequestBody @Valid ProjectGenerationTask projectGenerationTask) {
        return taskFacade.update(taskId, projectGenerationTask);
    }

    @DeleteMapping("{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        taskFacade.delete(taskId);
    }

    @PostMapping("{taskId}/execute")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void executeTask(@PathVariable String taskId) {
        taskFacade.executeTask(taskId);
    }

    @GetMapping(value = "{taskId}/result", produces = APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<FileSystemResource> getResult(@PathVariable String taskId) {
        return taskFacade.getTaskResult(taskId);
    }

}
