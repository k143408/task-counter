package com.task.challenge.exceptions;

public class FilePathNotFoundException extends RuntimeException {
    public FilePathNotFoundException() {
    }

    public FilePathNotFoundException(String message) {
        super(message);
    }
}
