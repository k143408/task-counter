package com.task.challenge.services.impl;

import com.task.challenge.model.CounterGenerationTask;
import com.task.challenge.model.ProjectGenerationTask;
import com.task.challenge.repository.ProjectGenerationTaskRepository;
import com.task.challenge.services.impl.internal.CounterTask;
import com.task.challenge.services.impl.internal.CounterTaskWithFutureReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CounterGenerationTaskServiceTest {

    @Mock
    private ProjectGenerationTaskRepository projectGenerationTaskRepository;

    @Mock
    private ThreadPoolTaskExecutor taskExecutor;

    private CounterGenerationTaskService taskService;

    @BeforeEach
    public void setup() {
        Mockito.clearInvocations(projectGenerationTaskRepository, taskExecutor);
        taskService = new CounterGenerationTaskService(projectGenerationTaskRepository, taskExecutor);

    }

    @Test
    public void delete_ShouldCancelTaskAndDeleteFromRepository() {
       
        String taskId = "1";

        taskService.delete(taskId);

        verify(projectGenerationTaskRepository, times(1)).deleteById(taskId);
        verifyNoMoreInteractions(projectGenerationTaskRepository);
    }

    @Test
    public void deleteUnExecutedTaskOlderThan_ShouldDeleteUnexecutedCounterTasksOlderThanThreshold() {
       
        LocalDate thresholdDate = LocalDate.now().minusDays(7);
        Date threshold = Date.from(thresholdDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<ProjectGenerationTask> unExecutedTasks = new ArrayList<>();
        CounterGenerationTask counterTask1 = new CounterGenerationTask();
        counterTask1.setId("1");
        CounterGenerationTask counterTask2 = new CounterGenerationTask();
        counterTask2.setId("2");
        unExecutedTasks.add(counterTask1);
        unExecutedTasks.add(counterTask2);

        when(projectGenerationTaskRepository.deleteUnExecutedTasksOlderThan(threshold)).thenReturn(unExecutedTasks);

        
        taskService.deleteUnExecutedTaskOlderThan(thresholdDate);

        
        verify(projectGenerationTaskRepository, times(1)).deleteUnExecutedTasksOlderThan(threshold);
        verify(projectGenerationTaskRepository, times(2)).deleteById(anyString());

        verifyNoMoreInteractions(projectGenerationTaskRepository);
    }

    @Test
    public void executeTask_WithCounterGenerationTask_ShouldExecuteCounterTask() {
       
        CounterGenerationTask counterTask = new CounterGenerationTask();
        counterTask.setId("1");

        
        taskService.executeTask(counterTask);

        
        verify(taskExecutor, times(1)).submit(any(Runnable.class));
        verifyNoMoreInteractions(taskExecutor);
    }

    @Test
    public void getTaskProgress_WithCounterGenerationTask_ShouldReturnTaskProgress() {
       
        String taskId = "1";
        CounterGenerationTask counterTask = new CounterGenerationTask();
        counterTask.setProgress(50);

        Map<String, CounterTaskWithFutureReference> runningTasks = (Map<String, CounterTaskWithFutureReference>) ReflectionTestUtils.getField(taskService, "runningTasks");

        runningTasks.put(taskId, new CounterTaskWithFutureReference(mock(CounterTask.class), mock(Future.class)));

        when(projectGenerationTaskRepository.findById(taskId)).thenReturn(Optional.of(counterTask));

        taskService.getTaskProgress(taskId);

        verify(projectGenerationTaskRepository, times(1)).findById(taskId);
        verifyNoMoreInteractions(projectGenerationTaskRepository);
    }

    @Test
    public void getTaskProgress_WithNonCounterGenerationTask_ShouldThrowUnsupportedOperationException() {
       
        String taskId = "1";
        ProjectGenerationTask task = new ProjectGenerationTask();

        when(projectGenerationTaskRepository.findById(anyString())).thenReturn(Optional.of(task));

       
        assertThatThrownBy(() -> taskService.getTaskProgress(taskId))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Does not support");
    }

    @Test
    public void cancelTask_WithRunningTask_ShouldCancelTaskAndRemoveFromRunningTasks() {
       
        String taskId = "1";
        CounterTask mockCounterTask = mock(CounterTask.class);
        Future<?> mockFuture = mock(Future.class);

        Map<String, CounterTaskWithFutureReference> runningTasks = (Map<String, CounterTaskWithFutureReference>) ReflectionTestUtils.getField(taskService, "runningTasks");
        CounterTaskWithFutureReference counterTaskWithFutureReference = new CounterTaskWithFutureReference(mockCounterTask, mockFuture);
        runningTasks.put(taskId, counterTaskWithFutureReference);

        
        taskService.cancelTask(taskId);

        
        verify(counterTaskWithFutureReference.getFuture(), times(1)).cancel(true);
    }
}