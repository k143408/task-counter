package com.task.challenge.services.impl.internal;

import com.task.challenge.model.CounterGenerationTask;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CounterTask implements Runnable {
    private final CounterGenerationTask task;
    private final ScheduledExecutorService executorService;
    public CounterTask(CounterGenerationTask task) {
        if (Objects.isNull(task)){
            throw new IllegalArgumentException("Task cannot be null");
        }
        this.task = task;
        this.task.setProgress(0);
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void run() {
        scheduleUpdate(task.getX(), task.getY());
    }
    private void scheduleUpdate(final int current, final int target) {
        executorService.schedule(() -> {
            if (current < target && !Thread.currentThread().isInterrupted()) {
                task.setProgress(task.getProgress().intValue() + 1);
                scheduleUpdate(task.getProgress().intValue(), target);
            } else {
                task.setProgress(target);
                executorService.shutdown();
            }
        }, 1, TimeUnit.SECONDS);
    }

    public Integer getProgress() {
        return task.getProgress();
    }
}
