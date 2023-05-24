package com.celonis.challenge.services.impl;

import com.celonis.challenge.model.CounterGenerationTask;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.repository.ProjectGenerationTaskRepository;
import com.celonis.challenge.services.impl.internal.CounterTask;
import com.celonis.challenge.services.TaskService;
import com.celonis.challenge.services.impl.internal.CounterTaskWithFutureReference;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

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
        super.delete(taskId);
    }

    @Override
    public void deleteUnExecutedTaskOlderThan(LocalDate thresholdDate) {
        Date threshold = Date.from(thresholdDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<ProjectGenerationTask> unExecutedTasks = projectGenerationTaskRepository.deleteUnExecutedTasksOlderThan(threshold);

        unExecutedTasks.stream().filter(t-> t instanceof CounterGenerationTask)
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
        ProjectGenerationTask task = get(taskId);
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
        CounterTaskWithFutureReference taskFuture = runningTasks.get(taskId);
        if (taskFuture != null) {
            taskFuture.getFuture().cancel(true);
            runningTasks.remove(taskId);
        }
    }

    private void executeCounterTask(CounterGenerationTask task) {
        CounterTask taskCounter = new CounterTask(task);
        Future<?> taskFuture = taskExecutor.submit(taskCounter);
        runningTasks.put(task.getId(), new CounterTaskWithFutureReference(taskCounter,taskFuture));
    }

}
