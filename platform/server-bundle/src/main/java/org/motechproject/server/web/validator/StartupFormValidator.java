package org.motechproject.server.web.validator;

import org.apache.commons.validator.UrlValidator;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Arrays;


public class StartupFormValidator implements Validator {
    private UrlValidator urlValidator;

    public StartupFormValidator() {
        urlValidator = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);
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
            String value = errors.getFieldValue(field).toString().replace("localhost", "127.0.0.1");

            if (errors.getFieldErrorCount(field) == 0 && !urlValidator.isValid(value)) {
                errors.rejectValue(field, String.format("error.invalid.%s", field), null, null);
            }
        }
    }

}
