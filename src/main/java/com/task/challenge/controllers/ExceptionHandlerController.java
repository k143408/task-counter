package com.task.challenge.controllers;

import com.task.challenge.exceptions.FilePathNotFoundException;
import com.task.challenge.exceptions.NotAuthorizedException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleNotFound(EntityNotFoundException e, WebRequest request) {
        return createErrorResponse("Not found", request);
    }

    @ExceptionHandler(FilePathNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleNotFound(FilePathNotFoundException e, WebRequest request) {
        return createErrorResponse(e.getMessage(), request);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> unSupportedOperationException(UnsupportedOperationException e, WebRequest request) {
        return createErrorResponse("Not found", request);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> emptyResultDataAccessException(EmptyResultDataAccessException e, WebRequest request) {
        return createErrorResponse("Not found", request);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Map<String, Object> handleNotAuthorized(WebRequest request) {
        return createErrorResponse("Not authorized", request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleInternalError(Exception e, WebRequest request) {
        return createErrorResponse("Internal error", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        return createErrorResponse(bindingResult.getAllErrors(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return createErrorResponse(ex.getMessage(), request);
    }



    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, WebRequest request) {
        return createErrorResponse(e.getMessage(), request);
    }

    private Map<String, Object> createErrorResponse(Object errorMessage, WebRequest request) {
        DefaultErrorAttributes defaultErrorAttributes = new DefaultErrorAttributes(true);
        Map<String, Object> errorAttributes = defaultErrorAttributes.getErrorAttributes(request,true);
        errorAttributes.put("message", errorMessage);
        return errorAttributes;
    }
}
