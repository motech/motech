package org.motechproject.server.web.validator;

import org.springframework.validation.Errors;

import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

/**
 * Validation utils that consists of common validations that can be used across multiple controllers.
 */
public final class ValidationUtils {
    private ValidationUtils() {
    }

    public static void validateEmptyOrWhitespace(Errors errors, String errorMessageFormat, String... fields) {
        for (String field : fields) {
            rejectIfEmptyOrWhitespace(errors, field, String.format(errorMessageFormat, field));
        }
    }
}
