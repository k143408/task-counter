package com.task.challenge.services.impl;

import com.task.challenge.model.CounterGenerationTask;
import com.task.challenge.model.ProjectGenerationTask;
import com.task.challenge.repository.ProjectGenerationTaskRepository;
import com.task.challenge.services.TaskService;
import com.task.challenge.services.impl.internal.CounterTask;
import com.task.challenge.services.impl.internal.CounterTaskWithFutureReference;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CounterGenerationTaskService extends ProjectGenerationTaskService implements TaskService {

    private final ThreadPoolTaskExecutor taskExecutor;
    private final Map<String, CounterTaskWithFutureReference> runningTasks;

    public CounterGenerationTaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository, ThreadPoolTaskExecutor taskExecutor) {
        super(projectGenerationTaskRepository);
        this.taskExecutor = taskExecutor;
        this.runningTasks = new ConcurrentHashMap<>();
    }

    @Override
    public void delete(String taskId) {
        cancelTask(taskId);
        runningTasks.remove(taskId);
        super.delete(taskId);
    }

    @Override
    public void deleteUnExecutedTaskOlderThan(LocalDate thresholdDate) {
        var threshold = Date.from(thresholdDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        var unExecutedTasks = projectGenerationTaskRepository.deleteUnExecutedTasksOlderThan(threshold);

        unExecutedTasks.stream()
                .map(ProjectGenerationTask::getId)
                .forEach(this::delete);
    }

    @Override
    public void executeTask(ProjectGenerationTask task) {
        if (task instanceof CounterGenerationTask){
            executeCounterTask((CounterGenerationTask) task);
        }
    }

    @Override
    public Integer getTaskProgress(String taskId) {
        var task = get(taskId);
        if (task instanceof CounterGenerationTask) {
            CounterTaskWithFutureReference counterTaskWithFutureReference = runningTasks.get(taskId);
            if (Objects.nonNull(counterTaskWithFutureReference)){
                return counterTaskWithFutureReference.getTask().getProgress();
            }
        }
        throw new UnsupportedOperationException("Does not support");
    }

    @Override
    public void cancelTask(String taskId) {
        var taskFuture = runningTasks.get(taskId);
        if (taskFuture != null) {
            taskFuture.getFuture().cancel(true);
        }

    }

    protected void executeCounterTask(CounterGenerationTask task) {
        var taskCounter = new CounterTask(task);
        var taskFuture = taskExecutor.submit(taskCounter);
        runningTasks.put(task.getId(), new CounterTaskWithFutureReference(taskCounter, taskFuture));
    }
}
