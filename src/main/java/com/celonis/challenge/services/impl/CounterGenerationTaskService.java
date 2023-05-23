package com.celonis.challenge.services.impl;

import com.celonis.challenge.model.CounterGenerationTask;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.repository.ProjectGenerationTaskRepository;
import com.celonis.challenge.services.CounterTask;
import com.celonis.challenge.services.TaskService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
public class CounterGenerationTaskService extends ProjectGenerationTaskService implements TaskService {

    private final ThreadPoolTaskExecutor taskExecutor;
    private final Map<String, Future<?>> runningTasks;


    public CounterGenerationTaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository, ThreadPoolTaskExecutor taskExecutor) {
        super(projectGenerationTaskRepository);
        this.taskExecutor = taskExecutor;
        this.runningTasks = new ConcurrentHashMap<>();
    }

    @Override
    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        ProjectGenerationTask task = super.createTask(projectGenerationTask);
        if (task instanceof CounterGenerationTask) {
            executeCounterTask((CounterGenerationTask) task);
        }
        return task;
    }

    private void executeCounterTask(CounterGenerationTask task) {
        CounterTask taskCounter = new CounterTask(task, projectGenerationTaskRepository);
        Future<?> taskFuture = taskExecutor.submit(taskCounter);
        runningTasks.put(task.getId(), taskFuture);
    }

    public Integer getTaskProgress(String taskId) {
        ProjectGenerationTask task = get(taskId);
        if (task instanceof CounterGenerationTask) {
            CounterGenerationTask counterTask = (CounterGenerationTask) task;
            return counterTask.getProgress();
        }
        throw new UnsupportedOperationException("Does not support");
    }

    public void cancelTask(String taskId) {
        Future<?> taskFuture = runningTasks.get(taskId);
        if (taskFuture != null) {
            taskFuture.cancel(true);
            runningTasks.remove(taskId);
        }
    }

}
