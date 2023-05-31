package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.FilePathNotFoundException;
import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.model.ProjectGenerationTask;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    public FileSystemResource getTaskResult(ProjectGenerationTask projectGenerationTask) {
        if (Strings.isBlank(projectGenerationTask.getStorageLocation())) {
            throw new FilePathNotFoundException("Task is not executed yet.");
        }
        Path inputFile = Path.of(projectGenerationTask.getStorageLocation());

        if (!Files.exists(inputFile)) {
            throw new FilePathNotFoundException("File does not exist.");
        }
        return new FileSystemResource(inputFile);
    }

    public ProjectGenerationTask storeResult(ProjectGenerationTask projectGenerationTask, Resource resource) {
        try {
            File outputFile = File.createTempFile(projectGenerationTask.getId(), ".zip");
            outputFile.deleteOnExit();
            projectGenerationTask.setStorageLocation(outputFile.getAbsolutePath());
            IOUtils.copy(resource.getInputStream(), new FileOutputStream(outputFile));
            return projectGenerationTask;
        } catch (IOException e) {
            throw new InternalException(e.getMessage());
        }
    }

    public void delete(String filePath) {
        try {
            Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            throw new InternalException(e.getMessage());
        }
    }
}
