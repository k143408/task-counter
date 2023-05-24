package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.model.ProjectGenerationTask;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class FileService {

    public ResponseEntity<FileSystemResource> getTaskResult(ProjectGenerationTask projectGenerationTask) {
        if (Objects.isNull(projectGenerationTask.getStorageLocation())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Path inputFile = Path.of(projectGenerationTask.getStorageLocation());

        if (!Files.exists(inputFile)) {
            throw new InternalException("File not generated yet");
        }

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");

        return new ResponseEntity<>(new FileSystemResource(inputFile), respHeaders, HttpStatus.OK);
    }

    public ProjectGenerationTask storeResult(ProjectGenerationTask projectGenerationTask, Resource resource) throws IOException {
        File outputFile = File.createTempFile(projectGenerationTask.getId(), ".zip");
        outputFile.deleteOnExit();
        projectGenerationTask.setStorageLocation(outputFile.getAbsolutePath());
        IOUtils.copy(resource.getInputStream(), new FileOutputStream(outputFile));
        return projectGenerationTask;
    }
}
