package com.Task.challenge.services;

import com.Task.challenge.exceptions.InternalException;
import com.Task.challenge.exceptions.NotFoundException;
import com.Task.challenge.model.ProjectGenerationTask;
import com.Task.challenge.model.ProjectGenerationTaskRepository;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    private final FileService fileService;
    
    public TaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository,
                       FileService fileService) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
        this.fileService = fileService;
    }

    public List<ProjectGenerationTask> listTasks() {
        return projectGenerationTaskRepository.findAll();
    }

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        projectGenerationTask.setId(null);
        projectGenerationTask.setCreationDate(new Date());
        return projectGenerationTaskRepository.save(projectGenerationTask);
    }

    public ProjectGenerationTask getTask(String taskId) {
        return get(taskId);
    }

    public ProjectGenerationTask update(String taskId, ProjectGenerationTask projectGenerationTask) {
        ProjectGenerationTask existing = get(taskId);
        existing.setCreationDate(projectGenerationTask.getCreationDate());
        existing.setName(projectGenerationTask.getName());
        return projectGenerationTaskRepository.save(existing);
    }

    public void delete(String taskId) {
        projectGenerationTaskRepository.deleteById(taskId);
    }

    public void executeTask(String taskId) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
        if (url == null) {
            throw new InternalException("Zip file not found");
        }
        try {
            fileService.storeResult(taskId, url);
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }

    private ProjectGenerationTask get(String taskId) {
        Optional<ProjectGenerationTask> projectGenerationTask = projectGenerationTaskRepository.findById(taskId);
        return projectGenerationTask.orElseThrow(NotFoundException::new);
    }
}
