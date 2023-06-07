package com.task.challenge.controllers;

import com.task.challenge.facade.TaskFacade;
import com.task.challenge.model.ProjectGenerationTask;
import com.task.challenge.request.TaskRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/tasks", produces = APPLICATION_JSON_VALUE)
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
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectGenerationTask createTask(@RequestBody @Valid TaskRequest taskRequest) {
        return taskFacade.createTask(taskRequest);
    }

    @GetMapping("{taskId}")
    public ProjectGenerationTask getTask(@PathVariable String taskId) {
        return taskFacade.getTask(taskId);
    }

    @PutMapping("{taskId}")
    public ProjectGenerationTask updateTask(@PathVariable String taskId,
                                            @RequestBody @Valid TaskRequest taskRequest) {
        return taskFacade.update(taskId, taskRequest);
    }

    @DeleteMapping("{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        taskFacade.delete(taskId);
    }

    @PostMapping("{taskId}")
    public ResponseEntity<?> executeTask(@PathVariable String taskId, @RequestParam("action") String action) {
        switch (action.toLowerCase()) {
            case "execute" -> {
                taskFacade.executeTask(taskId);
                return ResponseEntity.accepted().build();
            }
            case "result" -> {
                return getResultFromTask(taskId);
            }
            case "progress" -> {
                return ResponseEntity.ok().body(taskFacade.getTaskProgress(taskId));
            }
            case "cancel" -> {
                taskFacade.cancelTask(taskId);
                return ResponseEntity.accepted().build();
            }
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }
    }

    private ResponseEntity<FileSystemResource> getResultFromTask(String taskId) {
        FileSystemResource taskResult = taskFacade.getTaskResult(taskId);

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");

        return ResponseEntity.ok()
                .headers(respHeaders)
                .body(taskResult);
    }
}
