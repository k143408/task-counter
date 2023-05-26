package com.celonis.challenge.services.impl.internal;

import com.celonis.challenge.model.CounterGenerationTask;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class CounterTask implements Runnable {
    private final AtomicReference<CounterGenerationTask> task;

    public CounterTask(CounterGenerationTask task) {
        if (Objects.isNull(task)){
            throw new IllegalArgumentException("Task cannot be null");
        }
        this.task = new AtomicReference<>(task);
    }

    @Override
    public void run() {
        int current = task.get().getX();
        int target = task.get().getY();

        try {
            while (current < target) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                task.get().setProgress(current);

                current++;

                TimeUnit.SECONDS.sleep(1); // Wait for 1 second
            }
            task.get().setProgress(target);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Integer getProgress() {
        return task.get().getProgress();
    }
}
