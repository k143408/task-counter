package com.task.challenge.controllers;

import com.task.challenge.facade.TaskFacade;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"));


        verify(taskFacade, times(1)).listTasks();
        verifyNoMoreInteractions(taskFacade);
    }

    @Test
    public void createTask_ValidTaskRequest_ShouldReturnCreatedTask() throws Exception {

        ProjectGenerationTask createdTask = new ProjectGenerationTask();
        createdTask.setId("1");
        createdTask.setName("Task 1");

        when(taskFacade.createTask(any(TaskRequest.class))).thenReturn(createdTask);


        mockMvc.perform(post("/api/tasks/")
                        .content("{\"name\":\"Task 1\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
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
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Task 1"));


        verify(taskFacade, times(1)).getTask("1");
        verifyNoMoreInteractions(taskFacade);
    }


    @Test
    public void testExecuteTask_WithValidAction_Execute_ShouldReturnAccepted() throws Exception {
        String taskId = "task1";
        String action = "execute";

        mockMvc.perform(post("/api/tasks/{taskId}", taskId)
                        .param("action", action))
                .andExpect(status().isAccepted())
                .andExpect(content().string(""));

        // Verify that taskFacade.executeTask() is called with the correct arguments
        verify(taskFacade).executeTask(taskId);
    }

    @Test
    public void testExecuteTask_WithValidAction_Result_ShouldReturnTaskResult() throws Exception {
        String taskId = "task1";
        String action = "result";

        File taskResult = createTempFile();
        when(taskFacade.getTaskResult(taskId)).thenReturn(new FileSystemResource(taskResult));

        mockMvc.perform(post("/api/tasks/{taskId}", taskId)
                        .param("action", action))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"challenge.zip\""))
                .andExpect(content().bytes(Files.readAllBytes(Paths.get(taskResult.getAbsolutePath()))));

        verify(taskFacade).getTaskResult(taskId);

        deleteTempFile(taskResult);
    }

    @Test
    public void testExecuteTask_WithValidAction_Progress_ShouldReturnTaskProgress() throws Exception {
        String taskId = "task1";
        String action = "progress";

        // Mock the taskFacade.getTaskProgress() method to return a TaskProgressResponse
        TaskProgressResponse taskProgress = new TaskProgressResponse(0);
        when(taskFacade.getTaskProgress(taskId)).thenReturn(taskProgress);

        mockMvc.perform(post("/api/tasks/{taskId}", taskId)
                        .param("action", action))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"progress\":0}"));

        // Verify that taskFacade.getTaskProgress() is called with the correct arguments
        verify(taskFacade).getTaskProgress(taskId);
    }

    @Test
    public void testExecuteTask_WithValidAction_Cancel_ShouldReturnAccepted() throws Exception {
        String taskId = "task1";
        String action = "cancel";

        mockMvc.perform(post("/api/tasks/{taskId}", taskId)
                        .param("action", action))
                .andExpect(status().isAccepted())
                .andExpect(content().string(""));

        // Verify that taskFacade.cancelTask() is called with the correct arguments
        verify(taskFacade).cancelTask(taskId);
    }

    @Test
    public void testExecuteTask_WithInvalidAction_ShouldThrowIllegalArgumentException() throws Exception {
        String taskId = "task1";
        String action = "invalidAction";

        mockMvc.perform(post("/api/tasks/{taskId}", taskId)
                        .param("action", action))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid action: " + action)));
    }
    // Helper methods

    private File createTempFile() throws IOException {
        return Files.createTempFile("challenge", ".zip").toFile();
    }

    private void deleteTempFile(File file) {
        FileSystemUtils.deleteRecursively(file.getParentFile());
    }
}
