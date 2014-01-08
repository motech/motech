package org.motechproject.server.web.validator;

import org.motechproject.server.web.form.StartupForm;

import java.util.List;

import static org.motechproject.commons.date.util.StringUtil.isNullOrEmpty;

/**
 * Generic validator class that validates presence of a given field
 */
public class RequiredFieldValidator implements AbstractValidator {

    public static final String ERROR_REQUIRED = "server.error.required.%s";

    private String fieldName;
    private String fieldValue;

    public RequiredFieldValidator(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Override
    public void validate(StartupForm target, List<String> errors) {
        if (isNullOrEmpty(fieldValue)) {
            errors.add(String.format(ERROR_REQUIRED, fieldName));
        }
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
