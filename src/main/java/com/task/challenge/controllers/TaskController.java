package com.task.challenge.controllers;

import com.task.challenge.facade.TaskFacade;
import com.task.challenge.model.ProjectGenerationTask;
import com.task.challenge.request.TaskRequest;
import com.task.challenge.response.TaskProgressResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping("{taskId}/execute")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void executeTask(@PathVariable String taskId) {
        taskFacade.executeTask(taskId);
    }

    @GetMapping(value = "{taskId}/result", produces = APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<FileSystemResource> getResult(@PathVariable String taskId) {
        FileSystemResource taskResult = taskFacade.getTaskResult(taskId);

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");

        return new ResponseEntity<>(taskResult, respHeaders, HttpStatus.OK);
    }

    @GetMapping(value = "{taskId}/progress")
    @ResponseStatus(HttpStatus.OK)
    public TaskProgressResponse getProgress(@PathVariable String taskId) {
        return taskFacade.getTaskProgress(taskId);
    }

    @GetMapping(value = "{taskId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void getCancel(@PathVariable String taskId) {
        taskFacade.cancelTask(taskId);
    }

}
