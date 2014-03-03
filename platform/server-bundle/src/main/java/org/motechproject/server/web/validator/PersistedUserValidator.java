package org.motechproject.server.web.validator;

import org.apache.commons.validator.EmailValidator;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.form.StartupForm;

import java.util.List;

import static org.motechproject.commons.date.util.StringUtil.isNullOrEmpty;
import static org.motechproject.server.web.form.StartupForm.ADMIN_CONFIRM_PASSWORD;
import static org.motechproject.server.web.form.StartupForm.ADMIN_LOGIN;
import static org.motechproject.server.web.form.StartupForm.ADMIN_PASSWORD;

/**
 * Validates presence of admin user registration fields.
 * Checks existence of user with identical name
 * Checks existence of user with identical email
 * Checks that password and confirmed password field are same.
 */
public class PersistedUserValidator implements AbstractValidator {

    private static final String ERROR_REQUIRED = "server.error.required.%s";
    private MotechUserService userService;

    public PersistedUserValidator(MotechUserService userService) {
        this.userService = userService;
    }

    @Override
    public void validate(StartupForm target, List<String> errors, ConfigSource configSource) {
        // only validate without active admin user
        if (userService.hasActiveAdminUser()) {
            return;
        }

        if (isNullOrEmpty(target.getAdminLogin())) {
            errors.add(String.format(ERROR_REQUIRED, ADMIN_LOGIN));
        } else if (userService.hasUser(target.getAdminLogin())) {
            errors.add("server.error.user.exist");
        }

        if (isNullOrEmpty(target.getAdminPassword())) {
            errors.add(String.format(ERROR_REQUIRED, ADMIN_PASSWORD));
        } else if (isNullOrEmpty(target.getAdminConfirmPassword())) {
            errors.add(String.format(ERROR_REQUIRED, ADMIN_CONFIRM_PASSWORD));
        } else if (!target.getAdminPassword().equals(target.getAdminConfirmPassword())) {
            errors.add("server.error.invalid.password");
        }

        if (!EmailValidator.getInstance().isValid(target.getAdminEmail())) {
            errors.add("server.error.invalid.email");
        }

        UserDto user = userService.getUserByEmail(target.getAdminEmail());
        if (user != null && !user.getUserName().equals(target.getAdminLogin())) {
            errors.add("server.error.email.exist");
        }
    }
}
