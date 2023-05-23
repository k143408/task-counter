package com.celonis.challenge.validator;

import com.celonis.challenge.request.TaskRequest;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class TaskRequestValidator implements ConstraintValidator<TaskRequestConstraint, TaskRequest> {

    @Override
    public void initialize(TaskRequestConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(TaskRequest taskRequest, ConstraintValidatorContext context) {
        if (Objects.isNull(taskRequest)) {
            return false;
        }

        if (StringUtils.isEmpty(taskRequest.getName())) {
            return false;
        }

        if (taskRequest.getX() != null && taskRequest.getX() < 0) {
            return false;
        }

        if (taskRequest.getX() != null && taskRequest.getY() != null && taskRequest.getY() <= taskRequest.getX()) {
            return false;
        }

        if (taskRequest.getX() == null ^ taskRequest.getY() == null) {
            return false;
        }
        return true;
    }
}