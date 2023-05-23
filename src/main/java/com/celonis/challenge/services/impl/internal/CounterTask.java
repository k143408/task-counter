package com.celonis.challenge.services.impl.internal;

import com.celonis.challenge.model.CounterGenerationTask;
import com.celonis.challenge.repository.ProjectGenerationTaskRepository;

import java.util.concurrent.TimeUnit;

public class CounterTask implements Runnable {
    private final CounterGenerationTask task;
    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    public CounterTask(CounterGenerationTask task, ProjectGenerationTaskRepository projectGenerationTaskRepository) {
        this.task = task;
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
    }

    @Override
    public void run() {
        int current = task.getX();
        int target = task.getY();

        try {
            while (current < target) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                task.setProgress(current);
                projectGenerationTaskRepository.save(task);

                current++;

                TimeUnit.SECONDS.sleep(1); // Wait for 1 second
            }
            task.setProgress(target);
            projectGenerationTaskRepository.save(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
