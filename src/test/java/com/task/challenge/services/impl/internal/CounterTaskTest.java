package com.task.challenge.services.impl.internal;

import com.task.challenge.model.CounterGenerationTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CounterTaskTest {
    private CounterTask counterTask;

    @BeforeEach
    public void setup() {
        CounterGenerationTask task = new CounterGenerationTask();
        task.setX(0);
        task.setY(10);
        counterTask = new CounterTask(task);
    }

    @Test
    public void testConstructor_WithNullTask_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CounterTask(null);
        });
    }

    @Test
    public void testGetProgress_Initially_ShouldReturnZero() {
        assertEquals(0, counterTask.getProgress());
    }

    @Test
    public void testRun_AfterExecution_ShouldUpdateProgressToTargetValue() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.execute(counterTask);
        TimeUnit.SECONDS.sleep(2);
        assertEquals(1, counterTask.getProgress());
    }
}