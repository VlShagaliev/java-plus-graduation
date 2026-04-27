package ru.practicum.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SplitEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SplitEmailValidation {
    String message() default "Invalid email: incorrect format or part length";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}