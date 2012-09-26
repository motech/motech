package org.motechproject.server.web.validator;

import org.apache.commons.validator.routines.UrlValidator;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Arrays;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_ALL_SCHEMES;
import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

public class StartupFormValidator implements Validator {
    private UrlValidator urlValidator;

    public StartupFormValidator() {
        urlValidator = new UrlValidator(ALLOW_ALL_SCHEMES | ALLOW_LOCAL_URLS);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StartupForm.class.equals(clazz) || StartupSuggestionsForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        for (String field : Arrays.asList("language", "databaseUrl", "queueUrl")) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, String.format("error.required.%s", field));
        }

        for (String field : Arrays.asList("databaseUrl", "queueUrl")) {
            if (errors.getFieldErrorCount(field) == 0 && !urlValidator.isValid(errors.getFieldValue(field).toString())) {
                errors.rejectValue(field, String.format("error.invalid.%s", field), null, null);
            }
        }
    }

}
