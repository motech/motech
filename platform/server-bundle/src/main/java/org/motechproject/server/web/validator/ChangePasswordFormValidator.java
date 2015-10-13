package org.motechproject.server.web.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.web.form.ChangePasswordForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates the change password form.
 */
public class ChangePasswordFormValidator {

    public List<String> validate(ChangePasswordForm target) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(target.getUsername())) {
            errors.add("server.reset.noUsername");
        }

        if (StringUtils.isBlank(target.getOldPassword())) {
            errors.add("server.reset.oldPasswordRequired");
        }

        if (StringUtils.isBlank(target.getPassword())) {
            errors.add("server.reset.passwordRequired");
        }

        if (StringUtils.isBlank(target.getPasswordConfirmation())) {
            errors.add("server.reset.confirmationRequired");
        }

        if (errors.isEmpty() && !target.getPassword().equals(target.getPasswordConfirmation())) {
            errors.add("server.error.invalid.password");
        }

        if (errors.isEmpty() && target.getPassword().equals(target.getOldPassword())) {
            errors.add("server.reset.passwordCannotBeEqual");
        }

        return errors;
    }
}
