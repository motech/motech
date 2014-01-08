package org.motechproject.server.web.validator;

import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.web.form.StartupForm;

import java.util.List;

/**
 * Validator to validate user registration details.
 * Delegates to either @OpenIdUserValidator or @UserRegistrationValidator depending on login mode preference.
 */
public class UserRegistrationValidator implements AbstractValidator {

    private final PersistedUserValidator persistedUserValidator;
    private final OpenIdUserValidator openIdUserValidator;

    public UserRegistrationValidator(PersistedUserValidator persistedUserValidator, OpenIdUserValidator openIdUserValidator) {
        this.persistedUserValidator = persistedUserValidator;
        this.openIdUserValidator = openIdUserValidator;
    }

    @Override
    public void validate(StartupForm target, List<String> errors) {
        LoginMode loginMode = LoginMode.valueOf(target.getLoginMode());
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
