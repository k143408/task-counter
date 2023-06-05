package com.task.challenge.controllers;

import com.task.challenge.exceptions.FilePathNotFoundException;
import com.task.challenge.exceptions.NotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ErrorController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException e) {
        logger.error("Entity not found : {}", e.getMessage());
        return "Not found";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FilePathNotFoundException.class)
    public String handleNotFound(FilePathNotFoundException e) {
        logger.error("{}", e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UnsupportedOperationException.class)
    public String unSupportedOperationException(UnsupportedOperationException e) {
        logger.error("There is no support of this operation {}", e.getMessage());
        return "Not found";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public String emptyResultDataAccessException(EmptyResultDataAccessException e) {
        logger.error("Entity not found {}", e.getMessage());
        return "Not found";
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthorizedException.class)
    public String handleNotAuthorized() {
        logger.error("Not authorized");
        return "Not authorized";
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleInternalError(Exception e) {
        logger.error("Unhandled Exception in Controller", e);
        return "Internal error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        logger.error("Validation Error : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public String handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        logger.error("Media type is not supported {}", e.getMessage());
        return e.getMessage();
    }

}
