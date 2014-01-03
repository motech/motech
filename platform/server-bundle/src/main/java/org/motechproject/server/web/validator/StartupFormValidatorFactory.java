package org.motechproject.server.web.validator;

import org.apache.commons.validator.UrlValidator;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.stereotype.Component;

/**
 * Factory to create startUpFormValidator with requisite validators.
 * If Admin User exists,the admin user is not created so the relevant validators are not added.
 */
@Component
public class StartupFormValidatorFactory {

    public StartupFormValidator getStartupFormValidator(MotechUserService userService) {
        StartupFormValidator startupFormValidator = new StartupFormValidator();

        startupFormValidator.add(new RequiredFieldValidator(StartupForm.LANGUAGE));
        startupFormValidator.add(new RequiredFieldValidator(StartupForm.LOGIN_MODE));
        startupFormValidator.add(new QueueURLValidator());
        startupFormValidator.add(new UserRegistrationValidator(new PersistedUserValidator(userService),
                new OpenIdUserValidator(new UrlValidator())));

        return startupFormValidator;
    }
}
