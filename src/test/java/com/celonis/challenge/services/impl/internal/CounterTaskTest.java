package com.celonis.challenge.services.impl.internal;

import com.celonis.challenge.model.CounterGenerationTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CounterTaskTest {
    ThreadPoolTaskExecutor taskExecutor;
    @BeforeEach
    public void init(){
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.initialize();
    }

    @AfterEach
    public void shutdown(){
        taskExecutor.shutdown();
    }
    @Test
    public void run_ShouldUpdateTaskProgressUntilTargetIsReached() throws InterruptedException {
       
        CounterGenerationTask task = new CounterGenerationTask();
        task.setX(0);
        task.setY(5);
        CounterTask counterTask = new CounterTask(task);

        
        Thread thread = new Thread(counterTask);
        thread.start();
        thread.join();

        
        assertThat(task.getProgress()).isEqualTo(5);
    }

    @Test
    public void run_WithInterruptedThread_ShouldStopExecution() throws InterruptedException {
       
        CounterGenerationTask task = new CounterGenerationTask();
        task.setX(0);
        task.setY(10);
        CounterTask counterTask = new CounterTask(task);

        
        Thread thread = new Thread(counterTask);
        thread.start();
        TimeUnit.MILLISECONDS.sleep(50);
        thread.interrupt();
        thread.join();

        
        assertThat(task.getProgress()).isLessThan(10);
    }

    @Test
    public void getProgress_ShouldReturnTaskProgress() {
       
        CounterGenerationTask task = new CounterGenerationTask();
        task.setProgress(7);
        CounterTask counterTask = new CounterTask(task);

        
        Integer progress = counterTask.getProgress();

        
        assertThat(progress).isEqualTo(7);
    }

    @Test
    public void constructor_WithNullTask_ShouldThrowIllegalArgumentException() {
       
        assertThatThrownBy(() -> new CounterTask(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task cannot be null");
    }

}