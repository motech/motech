package org.motechproject.server.web.validator;

import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * StartupFormValidator validates user information during registration process
 */
@Component
public class StartupFormValidator {

    private List<AbstractValidator> validators = new ArrayList<>();

    public StartupFormValidator() {
    }

    public List<String> validate(StartupForm target, ConfigSource configSource) {
        List<String> errors = new ArrayList<>();

        for (AbstractValidator validator : validators) {
            validator.validate(target, errors, configSource);
        }

        return errors;
    }

    public void add(AbstractValidator validator) {
        validators.add(validator);
    }

    public List<AbstractValidator> getValidators() {
        return validators;
    }
}
