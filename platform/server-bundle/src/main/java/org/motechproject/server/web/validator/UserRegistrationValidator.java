package org.motechproject.server.web.validator;

import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator to validate user registration details.
 * Delegates to either @OpenIdUserValidator or @UserRegistrationValidator depending on login mode preference.
 */
public class UserRegistrationValidator implements Validator {

    private final PersistedUserValidator persistedUserValidator;
    private final OpenIdUserValidator openIdUserValidator;

    public UserRegistrationValidator(PersistedUserValidator persistedUserValidator, OpenIdUserValidator openIdUserValidator) {
        this.persistedUserValidator = persistedUserValidator;
        this.openIdUserValidator = openIdUserValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StartupForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Object loginModeValue = errors.getFieldValue(StartupForm.LOGIN_MODE);
        LoginMode loginMode = LoginMode.valueOf((String) loginModeValue);
        if (loginMode == null) {
            return;
        }

        if (loginMode.isOpenId()) {
            openIdUserValidator.validate(target, errors);
        } else {
            persistedUserValidator.validate(target, errors);
        }
    }
}
