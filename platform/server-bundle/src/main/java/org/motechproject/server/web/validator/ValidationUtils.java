package org.motechproject.server.web.validator;

import org.apache.commons.validator.UrlValidator;
import org.springframework.validation.Errors;

import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

/**
 * Validation utils that consists of common validations that can be used across multiple controllers.
 */
public final class ValidationUtils {
    private ValidationUtils() {
    }

    public static void validateUrl(Errors errors, String dbUrlField) {
        String value = errors.getFieldValue(dbUrlField).toString().replace("localhost", "127.0.0.1");
        UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);

        if (!urlValidator.isValid(value)) {
            errors.rejectValue(dbUrlField, String.format("server.error.invalid.%s", dbUrlField));
        }
    }

    public static void validateEmptyOrWhitespace(Errors errors, String errorMessageFormat, String... fields) {
        for (String field : fields) {
            rejectIfEmptyOrWhitespace(errors, field, String.format(errorMessageFormat, field));
        }
    }
}
