package com.celonis.challenge.util;

import com.celonis.challenge.model.CounterGenerationTask;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.request.TaskRequest;
import org.springframework.stereotype.Component;

@Component
public class TaskConvertUtil {
    private TaskConvertUtil() {}

    public static ProjectGenerationTask convert(TaskRequest tr) {
        if (tr.getX() != null) {
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
