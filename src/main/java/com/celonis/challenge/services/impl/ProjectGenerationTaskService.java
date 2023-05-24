package com.celonis.challenge.services.impl;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.repository.ProjectGenerationTaskRepository;
import com.celonis.challenge.services.TaskService;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

abstract class ProjectGenerationTaskService implements TaskService {

    protected final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    public ProjectGenerationTaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
    }

    @Override
    public List<ProjectGenerationTask> listTasks() {
        return projectGenerationTaskRepository.findAll();
    }

    @Override
    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        projectGenerationTask.setId(null);
        projectGenerationTask.setCreationDate(new Date());
        return projectGenerationTaskRepository.save(projectGenerationTask);
    }

    @Override
    public ProjectGenerationTask getTask(String taskId) {
        return get(taskId);
    }

    @Override
    public ProjectGenerationTask update(String taskId, ProjectGenerationTask projectGenerationTask) {
        ProjectGenerationTask existing = get(taskId);
        existing.setCreationDate(Optional.ofNullable(projectGenerationTask.getCreationDate()).orElseGet(Date::new));
        existing.setName(projectGenerationTask.getName());
        existing.setStorageLocation(projectGenerationTask.getStorageLocation());
        return projectGenerationTaskRepository.save(existing);
    }

    @Override
    public void delete(String taskId) {
        projectGenerationTaskRepository.deleteById(taskId);
    }



    protected ProjectGenerationTask get(String taskId) {
        Optional<ProjectGenerationTask> projectGenerationTask = projectGenerationTaskRepository.findById(taskId);
        return projectGenerationTask.orElseThrow(EntityNotFoundException::new);
    }
}
