package org.motechproject.server.web.validator;

import org.motechproject.server.web.form.ResetForm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

import static org.motechproject.commons.date.util.StringUtil.isNullOrEmpty;

@Component
public class ResetFormValidator {

    public List<String> validate(ResetForm target) {
        List<String> errors = new ArrayList<>();

        if (isNullOrEmpty(target.getPassword())) {
            errors.add("server.reset.passwordRequired");
        }

        if (isNullOrEmpty(target.getPasswordConfirmation())) {
            errors.add("server.reset.confirmationRequired");
        }

        if (errors.isEmpty() && !target.getPassword().equals(target.getPasswordConfirmation())) {
            errors.add("server.reset.samePassword");
        }

        return errors;
    }
}
