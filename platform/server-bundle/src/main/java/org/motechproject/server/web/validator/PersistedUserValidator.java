package org.motechproject.server.web.validator;

import org.apache.commons.validator.EmailValidator;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;

import static org.motechproject.server.web.form.StartupForm.ADMIN_CONFIRM_PASSWORD;
import static org.motechproject.server.web.form.StartupForm.ADMIN_EMAIL;
import static org.motechproject.server.web.form.StartupForm.ADMIN_LOGIN;
import static org.motechproject.server.web.form.StartupForm.ADMIN_PASSWORD;

/**
 * Validates presence of admin user registration fields.
 * Checks existence of user with identical name
 * Checks existence of user with identical email
 * Checks that password and confirmed password field are same.
 */
public class PersistedUserValidator implements Validator {

    private static final String ERROR_REQUIRED = "server.error.required.%s";
    private MotechUserService userService;

    public PersistedUserValidator(MotechUserService userService) {
        this.userService = userService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        // only validate without active admin user
        if (userService.hasActiveAdminUser()) {
            return;
        }

        String login = errors.getFieldValue(ADMIN_LOGIN).toString();
        String password = errors.getFieldValue(ADMIN_PASSWORD).toString();
        String passwordConfirm = errors.getFieldValue(ADMIN_CONFIRM_PASSWORD).toString();
        String adminEmail = errors.getFieldValue(ADMIN_EMAIL).toString();

        validateRequiredFields(errors);

        if (errors.getFieldErrorCount(login) == 0 && userService.hasUser(login)) {
            errors.rejectValue(ADMIN_LOGIN, "server.error.user.exist", null, null);
        }

        if (errors.getFieldErrorCount(password) == 0 && errors.getFieldErrorCount(passwordConfirm) == 0 && !password.equals(passwordConfirm)) {
            errors.rejectValue(ADMIN_PASSWORD, "server.error.invalid.password", null, null);
        }

        if (!EmailValidator.getInstance().isValid(adminEmail)) {
            errors.rejectValue(ADMIN_EMAIL, "server.error.invalid.email", null, null);
        }

        UserDto user = userService.getUserByEmail(adminEmail);
        if (user != null && !user.getUserName().equals(login)) {
            errors.rejectValue(ADMIN_EMAIL, "server.error.email.exist", null, null);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StartupForm.class.equals(clazz);
    }

    private void validateRequiredFields(Errors errors) {
        for (String field : Arrays.asList(ADMIN_LOGIN, ADMIN_PASSWORD, ADMIN_CONFIRM_PASSWORD)) {
            org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, String.format(ERROR_REQUIRED, field));
        }
    }
}
