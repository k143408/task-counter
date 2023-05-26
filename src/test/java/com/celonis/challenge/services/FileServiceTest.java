package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.FilePathNotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileServiceTest {

    @Test
    public void getTaskResult_WithNullStorageLocation_ShouldReturnNotFoundResponse() {

        FileService fileService = new FileService();

        ProjectGenerationTask projectGenerationTask = new ProjectGenerationTask();

        Assertions.assertThrows(FilePathNotFoundException.class, ()-> fileService.getTaskResult(projectGenerationTask));

    }

    @Test
    public void getTaskResult_WithNonExistingFile_ShouldThrowInternalException() {

        FileService fileService = new FileService();
        String storageLocation = "path/to/non_existing_file.zip";
        ProjectGenerationTask projectGenerationTask = new ProjectGenerationTask();
        projectGenerationTask.setStorageLocation(storageLocation);


        assertThatThrownBy(() -> fileService.getTaskResult(projectGenerationTask))
                .isInstanceOf(FilePathNotFoundException.class)
                .hasMessage("File does not exist.");

    }

    @Test
    public void getTaskResult_WithExistingFile_ShouldReturnFileResource() throws IOException {

        FileService fileService = new FileService();
        File tempFile = createTempFile();
        String storageLocation = tempFile.getAbsolutePath();

        ProjectGenerationTask projectGenerationTask = new ProjectGenerationTask();
        projectGenerationTask.setStorageLocation(storageLocation);


        FileSystemResource result = fileService.getTaskResult(projectGenerationTask);

        Assertions.assertEquals(tempFile, result.getFile());

        deleteTempFile(tempFile);
    }

    @Test
    public void storeResult_ShouldStoreResourceInTempFileAndReturnUpdatedTask() throws IOException {

        FileService fileService = new FileService();
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setId("123");
        ByteArrayResource byteArrayResource = new ByteArrayResource("Test".getBytes());

        ProjectGenerationTask result = fileService.storeResult(task, byteArrayResource);


        assertThat(result).isSameAs(task);
        assertThat(result.getStorageLocation()).isNotNull();


        deleteTempFile(new File(result.getStorageLocation()));
    }

    // Helper methods

    private File createTempFile() throws IOException {
        return Files.createTempFile("test", ".zip").toFile();
    }

    private void deleteTempFile(File file) {
        file.delete();
    }
}
