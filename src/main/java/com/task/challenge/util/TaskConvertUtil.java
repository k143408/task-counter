package com.task.challenge.util;

import com.task.challenge.model.CounterGenerationTask;
import com.task.challenge.model.ProjectGenerationTask;
import com.task.challenge.request.TaskRequest;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TaskConvertUtil {
    private TaskConvertUtil() {}

    public static ProjectGenerationTask convert(TaskRequest tr) {
        if (Objects.nonNull(tr.getX())) {
            CounterGenerationTask cg = new CounterGenerationTask();
            cg.setName(tr.getName());
            cg.setX(tr.getX());
            cg.setY(tr.getY());
            return cg;
        }
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setName(tr.getName());
        return task;
    }
}
