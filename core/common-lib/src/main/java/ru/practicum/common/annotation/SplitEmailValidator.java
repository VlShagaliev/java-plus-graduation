package ru.practicum.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

public class SplitEmailValidator implements ConstraintValidator<SplitEmailValidation, String> {
    private static final int LOCAL_PART_SIZE = 64;
    private static final int DOMAIN_PART_SIZE = 63;
    private static final EmailValidator emailValidator = new EmailValidator();

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank() || !emailValidator.isValid(email, context)) {
            return false;
        }
        final String[] emailParts = email.split("@");
        final String localPart = emailParts[0];

        if (localPart.length() > LOCAL_PART_SIZE) {
            return false;
        }
        final String domain = emailParts[1].split("\\.")[0];

        return domain.length() <= DOMAIN_PART_SIZE;
    }
}