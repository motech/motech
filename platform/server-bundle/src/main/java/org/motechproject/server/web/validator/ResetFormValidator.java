package org.motechproject.server.web.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.web.form.ResetForm;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ResetFormValidator {

    public List<String> validate(ResetForm target) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(target.getPassword())) {
            errors.add("server.reset.passwordRequired");
        }

        if (StringUtils.isBlank(target.getPasswordConfirmation())) {
            errors.add("server.reset.confirmationRequired");
        }

        if (errors.isEmpty() && !target.getPassword().equals(target.getPasswordConfirmation())) {
            errors.add("server.reset.samePassword");
        }

        return errors;
    }
}
