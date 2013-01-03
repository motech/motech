package org.motechproject.server.web.validator;

import org.apache.commons.validator.EmailValidator;
import org.apache.commons.validator.UrlValidator;
import org.motechproject.security.helper.AuthenticationMode;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Arrays;


public class StartupFormValidator implements Validator {
    private UrlValidator urlValidator;

    private final static String ERROR_REQUIRED = "error.required.%s";
    private final static String PROVIDER_NAME = "providerName";
    private final static String PROVIDER_URL = "providerUrl";
    private final static String LOGIN_MODE = "loginMode";

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
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, String.format(ERROR_REQUIRED, field));
        }

        for (String field : Arrays.asList("databaseUrl", "queueUrl")) {
            String value = errors.getFieldValue(field).toString().replace("localhost", "127.0.0.1");

            if (errors.getFieldErrorCount(field) == 0 && !urlValidator.isValid(value)) {
                errors.rejectValue(field, String.format("error.invalid.%s", field), null, null);
            }
        }

        if (AuthenticationMode.REPOSITORY.equals(errors.getFieldValue("loginMode").toString())) {
            validateRepository(errors);
        } else if (AuthenticationMode.OPEN_ID.equals(errors.getFieldValue("loginMode").toString())) {
            validateOpenId(errors);
        } else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, LOGIN_MODE, String.format(ERROR_REQUIRED, LOGIN_MODE));
        }
    }

    private void validateOpenId(Errors errors) {
        String providerUrl = errors.getFieldValue(PROVIDER_URL).toString();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PROVIDER_NAME, String.format(ERROR_REQUIRED, PROVIDER_NAME));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PROVIDER_URL, String.format(ERROR_REQUIRED, PROVIDER_URL));

        if (errors.getFieldErrorCount(PROVIDER_URL) == 0 && !urlValidator.isValid(providerUrl)) {
            errors.rejectValue(PROVIDER_URL, String.format("error.invalid.%s", PROVIDER_URL), null, null);
        }
    }

    private void validateRepository(Errors errors) {
        String password = errors.getFieldValue("adminPassword").toString();
        String passwordConfirm = errors.getFieldValue("adminConfirmPassword").toString();
        String adminEmail = errors.getFieldValue("adminEmail").toString();

        for (String field : Arrays.asList("adminLogin", "adminPassword", "adminConfirmPassword")) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, String.format(ERROR_REQUIRED, field));
        }

        if (errors.getFieldErrorCount(password) == 0 && errors.getFieldErrorCount(passwordConfirm) == 0 && !password.equals(passwordConfirm)) {
            errors.rejectValue("adminPassword", "error.invalid.password", null, null);
        }

        if (!EmailValidator.getInstance().isValid(adminEmail)) {
            errors.rejectValue("adminEmail", "error.invalid.email", null, null);
        }
    }
}
