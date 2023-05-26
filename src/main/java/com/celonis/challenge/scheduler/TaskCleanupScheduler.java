package com.celonis.challenge.scheduler;

import com.celonis.challenge.facade.TaskFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TaskCleanupScheduler {
    private final TaskFacade taskFacade;
    private final Long days;
    public TaskCleanupScheduler(TaskFacade taskFacade, @Value("${cleanup.days}") Long days) {
        this.taskFacade = taskFacade;
        this.days = days;
    }

    @Scheduled(cron = "${scheduler.clean_up.cron}" )
    public void cleanupTasks() {
        LocalDate thresholdDate = LocalDate.now().minusDays(days);
        taskFacade.deleteUnExecutedTasksOlderThan(thresholdDate);
    }
}
