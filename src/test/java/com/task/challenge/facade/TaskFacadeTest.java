package com.task.challenge.facade;

import com.task.challenge.model.ProjectGenerationTask;
import com.task.challenge.request.TaskRequest;
import com.task.challenge.services.FileService;
import com.task.challenge.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TaskFacadeTest {
    @Mock
    private TaskService taskService;

    @Mock
    private FileService fileService;

    @Mock
    private Resource resource;

    private TaskFacade taskFacade;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        taskFacade = new TaskFacade(taskService, fileService, resource);
    }

    @Test
    public void getTaskResult_ValidTaskId_ShouldReturnFileResource() {
       
        String taskId = "1";
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setId(taskId);
        when(taskService.getTask(taskId)).thenReturn(task);

        FileSystemResource fileResource = new FileSystemResource("path/to/file.zip");
        when(fileService.getTaskResult(task)).thenReturn(fileResource);

        FileSystemResource result = taskFacade.getTaskResult(taskId);

        assertEquals(result, fileResource);

        
        verify(taskService, times(1)).getTask(taskId);
        verify(fileService, times(1)).getTaskResult(task);
        verifyNoMoreInteractions(taskService, fileService);
    }

    @Test
    public void listTasks_ShouldReturnListOfTasks() {
       
        ProjectGenerationTask task1 = new ProjectGenerationTask();
        task1.setId("1");
        ProjectGenerationTask task2 = new ProjectGenerationTask();
        task2.setId("2");
        List<ProjectGenerationTask> tasks = Arrays.asList(task1, task2);
        when(taskService.listTasks()).thenReturn(tasks);

        
        List<ProjectGenerationTask> result = taskFacade.listTasks();

        
        assertEquals(result, tasks);

        
        verify(taskService, times(1)).listTasks();
        verifyNoMoreInteractions(taskService);
    }

    @Test
    public void createTask_ValidTaskRequest_ShouldReturnCreatedTask() {
       
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Task 1");

        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setId("1");
        task.setName("Task 1");

        when(taskService.createTask(any(ProjectGenerationTask.class))).thenReturn(task);

        
        ProjectGenerationTask result = taskFacade.createTask(taskRequest);


        assertEquals(result, task);

        
        verify(taskService, times(1)).createTask(any(ProjectGenerationTask.class));
        verifyNoMoreInteractions(taskService);
    }

    // Write similar test cases for other methods in TaskFacade

    @Test
    public void deleteUnExecutedTasksOlderThan_ShouldInvokeTaskService() {
       
        LocalDate thresholdDate = LocalDate.now();

        
        taskFacade.deleteUnExecutedTasksOlderThan(thresholdDate);

        
        verify(taskService, times(1)).deleteUnExecutedTaskOlderThan(thresholdDate);
        verifyNoMoreInteractions(taskService);
    }

}