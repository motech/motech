package org.motechproject.server.web.validator;

import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * StartupFormValidator validate user information during registration process
 */
public class StartupFormValidator implements Validator {

    private List<Validator> validators = new ArrayList<>();

    public StartupFormValidator() {
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StartupForm.class.equals(clazz) || StartupSuggestionsForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        for (Validator validator : validators) {
            validator.validate(target, errors);
        }
    }

    public void add(Validator validator) {
        validators.add(validator);
    }

    public List<Validator> getValidators() {
        return validators;
    }
}
