package com.task.challenge.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TaskRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskRequestConstraint {
    String message() default "Invalid TaskRequest";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}