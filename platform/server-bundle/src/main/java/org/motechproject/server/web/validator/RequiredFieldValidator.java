package org.motechproject.server.web.validator;

import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Generic validator class that validates presence of a given field
 */
public class RequiredFieldValidator implements Validator {

    public static final String ERROR_REQUIRED = "server.error.required.%s";

    private String fieldName;

    public RequiredFieldValidator(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, String.format(ERROR_REQUIRED, fieldName));
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StartupForm.class.equals(clazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RequiredFieldValidator that = (RequiredFieldValidator) o;

        return fieldName.equals(that.fieldName);
    }

    @Override
    public int hashCode() {
        return fieldName != null ? fieldName.hashCode() : 0;
    }
}
