package org.motechproject.server.web.validator;

import org.junit.Test;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserRegistrationValidatorTest {

    @Test
    public void shouldValidateUserDetailsWithOpenIdValidatorWhenLoginModeIsOpenId() {
        PersistedUserValidator persistedUserValidator = mock(PersistedUserValidator.class);
        OpenIdUserValidator openIdUserValidator = mock(OpenIdUserValidator.class);


        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("loginMode")).thenReturn("openId");

        StartupForm target = new StartupForm();

        UserRegistrationValidator userRegistrationValidator = new UserRegistrationValidator(persistedUserValidator, openIdUserValidator);
        userRegistrationValidator.validate(target, errors);

        verify(openIdUserValidator).validate(target, errors);
        verify(persistedUserValidator, never()).validate(target, errors);
    }

    @Test
    public void shouldValidateUserDetailsWithPersistentUserWhenLoginModeIsRepository() {
        PersistedUserValidator persistedUserValidator = mock(PersistedUserValidator.class);
        OpenIdUserValidator openIdUserValidator = mock(OpenIdUserValidator.class);


        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("loginMode")).thenReturn("repository");

        StartupForm target = new StartupForm();

        UserRegistrationValidator userRegistrationValidator = new UserRegistrationValidator(persistedUserValidator, openIdUserValidator);
        userRegistrationValidator.validate(target, errors);

        verify(persistedUserValidator).validate(target, errors);
        verify(openIdUserValidator, never()).validate(target, errors);
    }

    @Test
    public void shouldNotValidateUserDetailsWhenLoginModeIsNull() {
        PersistedUserValidator persistedUserValidator = mock(PersistedUserValidator.class);
        OpenIdUserValidator openIdUserValidator = mock(OpenIdUserValidator.class);


        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("loginMode")).thenReturn(null);

        StartupForm target = new StartupForm();

        UserRegistrationValidator userRegistrationValidator = new UserRegistrationValidator(persistedUserValidator, openIdUserValidator);
        userRegistrationValidator.validate(target, errors);

        verify(persistedUserValidator, never()).validate(target, errors);
        verify(openIdUserValidator, never()).validate(target, errors);
    }

}
