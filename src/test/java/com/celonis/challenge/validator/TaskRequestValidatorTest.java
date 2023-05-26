package com.celonis.challenge.validator;

import com.celonis.challenge.request.TaskRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TaskRequestValidatorTest {

    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final TaskRequestValidator validator = new TaskRequestValidator();

    @Test
    public void isValid_WithNullTaskRequest_ShouldReturnFalse() {
        
        boolean isValid = validator.isValid(null, context);

        
        assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @MethodSource("invalidTaskRequests")
    public void isValid_WithInvalidTaskRequest_ShouldReturnFalse(TaskRequest taskRequest) {
        
        boolean isValid = validator.isValid(taskRequest, context);

        
        assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @MethodSource("validTaskRequests")
    public void isValid_WithValidTaskRequest_ShouldReturnTrue(TaskRequest taskRequest) {
        
        boolean isValid = validator.isValid(taskRequest, context);

        
        assertThat(isValid).isTrue();
    }

    private static Stream<TaskRequest> invalidTaskRequests() {
        return Stream.of(
                createTaskRequest("", 5, 10), // Empty name
                createTaskRequest("Test Task", -1, 10), // Negative X
                createTaskRequest("Test Task", 5, 3), // Y <= X
                createTaskRequest("Test Task", 5, null), // X not null, Y null
                createTaskRequest("Test Task", null, 10), // X null, Y not null
                createTaskRequest("Test Task", 0, 0) // X = Y
        );
    }

    private static Stream<TaskRequest> validTaskRequests() {
        return Stream.of(
                createTaskRequest("Test Task", 3, 7),
                createTaskRequest("Test Task", 0, 1),
                createTaskRequest("Test Task", null, null)
        );
    }

    private static TaskRequest createTaskRequest(String name, Integer x, Integer y) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName(name);
        taskRequest.setX(x);
        taskRequest.setY(y);
        return taskRequest;
    }
}
