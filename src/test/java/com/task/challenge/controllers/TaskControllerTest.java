package com.task.challenge.controllers;

import com.task.challenge.facade.TaskFacade;
import com.task.challenge.model.CounterGenerationTask;
import com.task.challenge.model.ProjectGenerationTask;
import com.task.challenge.request.TaskRequest;
import com.task.challenge.response.TaskProgressResponse;
import com.task.challenge.security.SimpleHeaderFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@WebMvcTest(value = TaskController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SimpleHeaderFilter.class)
})
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskFacade taskFacade;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void listTasks_ShouldReturnListOfTasks() throws Exception {

        ProjectGenerationTask task1 = new ProjectGenerationTask();
        task1.setId("1");
        ProjectGenerationTask task2 = new ProjectGenerationTask();
        task2.setId("2");
        List<ProjectGenerationTask> tasks = Arrays.asList(task1, task2);
        when(taskFacade.listTasks()).thenReturn(tasks);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"));


        verify(taskFacade, times(1)).listTasks();
        verifyNoMoreInteractions(taskFacade);
    }

    @Test
    public void createTask_ValidTaskRequest_ShouldReturnCreatedTask() throws Exception {

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Task 1");

        ProjectGenerationTask createdTask = new ProjectGenerationTask();
        createdTask.setId("1");
        createdTask.setName("Task 1");

        when(taskFacade.createTask(any(TaskRequest.class))).thenReturn(createdTask);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks/")
                        .content("{\"name\":\"Task 1\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Task 1"));


        verify(taskFacade, times(1)).createTask(any(TaskRequest.class));
        verifyNoMoreInteractions(taskFacade);
    }

    @Test
    public void getTask_ExistingTaskId_ShouldReturnTask() throws Exception {

        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setId("1");
        task.setName("Task 1");

        when(taskFacade.getTask("1")).thenReturn(task);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Task 1"));


        verify(taskFacade, times(1)).getTask("1");
        verifyNoMoreInteractions(taskFacade);
    }

    // Similar test cases for other controller methods

    @Test
    public void getResult_ExistingTaskId_ShouldReturnFileResource() throws Exception {

        String taskId = "1";
        File tempFile = createTempFile();

        when(taskFacade.getTaskResult(taskId)).thenReturn(new FileSystemResource(tempFile));


        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/1/result")
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"challenge.zip\""));


        verify(taskFacade, times(1)).getTaskResult(taskId);
        verifyNoMoreInteractions(taskFacade);


        deleteTempFile(tempFile);
    }

    @Test
    public void getProgress_ExistingTaskId_ShouldReturnTaskProgressResponse() throws Exception {

        CounterGenerationTask task = new CounterGenerationTask();
        task.setId("1");
        task.setProgress(50);

        when(taskFacade.getTaskProgress("1")).thenReturn(new TaskProgressResponse(50));


        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/1/progress")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.progress").value(50));


        verify(taskFacade, times(1)).getTaskProgress("1");
        verifyNoMoreInteractions(taskFacade);
    }

    @Test
    public void cancelTask_ExistingTaskId_ShouldReturnHttpStatusOk() throws Exception {

        String taskId = "1";


        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/1/cancel"))
                .andExpect(MockMvcResultMatchers.status().isOk());


        verify(taskFacade, times(1)).cancelTask(taskId);
        verifyNoMoreInteractions(taskFacade);
    }

    // Helper methods

    private File createTempFile() throws IOException {
        return Files.createTempFile("challenge", ".zip").toFile();
    }

    private void deleteTempFile(File file) {
        FileSystemUtils.deleteRecursively(file.getParentFile());
    }
}
