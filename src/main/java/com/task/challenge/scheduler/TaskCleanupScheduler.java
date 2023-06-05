package com.task.challenge.scheduler;

import com.task.challenge.facade.TaskFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TaskCleanupScheduler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TaskFacade taskFacade;
    private final Long days;
    public TaskCleanupScheduler(TaskFacade taskFacade, @Value("${cleanup.days}") Long days) {
        this.taskFacade = taskFacade;
        this.days = days;
    }

    @Scheduled(cron = "${scheduler.clean_up.cron}" )
    public void cleanupTasks() {
        logger.info("Clean up task started: {}", LocalDateTime.now());
        var thresholdDate = LocalDate.now().minusDays(days);
        taskFacade.deleteUnExecutedTasksOlderThan(thresholdDate);
        logger.info("Clean up task ended: {}", LocalDateTime.now());
    }
}
