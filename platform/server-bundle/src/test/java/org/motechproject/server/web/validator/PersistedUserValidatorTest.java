package org.motechproject.server.web.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PersistedUserValidatorTest {

    private static final String EMPTY = "";

    @Mock
    private MotechUserService userService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldValidateFieldsAndRejectEmptyFields() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("adminLogin")).thenReturn(EMPTY);
        when(errors.getFieldValue("adminPassword")).thenReturn(EMPTY);
        when(errors.getFieldValue("adminConfirmPassword")).thenReturn(EMPTY);
        when(errors.getFieldValue("adminEmail")).thenReturn(EMPTY);


        persistedUserValidator.validate(new StartupForm(), errors);

        verify(errors).rejectValue("adminLogin", "server.error.required.adminLogin", null, null);
        verify(errors).rejectValue("adminPassword", "server.error.required.adminPassword", null, null);
        verify(errors).rejectValue("adminConfirmPassword", "server.error.required.adminConfirmPassword", null, null);
        verify(errors).rejectValue("adminEmail", "server.error.invalid.email", null, null);

        verify(errors, never()).rejectValue("adminPassword", "server.error.invalid.password", null, null);
    }

    @Test
    public void shouldRejectInvalidEmail() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("adminLogin")).thenReturn("admin");
        when(errors.getFieldValue("adminPassword")).thenReturn("password");
        when(errors.getFieldValue("adminConfirmPassword")).thenReturn("Password");
        when(errors.getFieldValue("adminEmail")).thenReturn("admin@motech");

        when(userService.hasUser("admin")).thenReturn(false);


        persistedUserValidator.validate(new StartupForm(), errors);

        verify(errors).rejectValue("adminEmail", "server.error.invalid.email", null, null);
    }

    @Test
    public void shouldRejectPasswordIfConfirmPasswordValueIsDifferent() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("adminLogin")).thenReturn("admin");
        when(errors.getFieldValue("adminPassword")).thenReturn("password");
        when(errors.getFieldValue("adminConfirmPassword")).thenReturn("Password");
        when(errors.getFieldValue("adminEmail")).thenReturn("admin@motech.org");

        when(userService.hasUser("admin")).thenReturn(false);


        persistedUserValidator.validate(new StartupForm(), errors);

        //If password is empty do not check against confirmPassword as empty password error is already added
        verify(errors).rejectValue("adminPassword", "server.error.invalid.password", null, null);
    }


    @Test
    public void shouldRejectUserIfUserExists() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("adminLogin")).thenReturn("admin");
        when(errors.getFieldValue("adminPassword")).thenReturn("password");
        when(errors.getFieldValue("adminConfirmPassword")).thenReturn("password");
        when(errors.getFieldValue("adminEmail")).thenReturn("admin@motech.org");

        when(userService.hasUser("admin")).thenReturn(true);

        persistedUserValidator.validate(new StartupForm(), errors);

        verify(errors).rejectValue("adminLogin", "server.error.user.exist", null, null);
    }

    @Test
    public void shouldRejectEmailIfInUse() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("adminLogin")).thenReturn("admin");
        when(errors.getFieldValue("adminPassword")).thenReturn("password");
        when(errors.getFieldValue("adminConfirmPassword")).thenReturn("password");
        when(errors.getFieldValue("adminEmail")).thenReturn("admin@motech.org");

        when(userService.hasUser("admin")).thenReturn(false);
        UserDto user = new UserDto();
        user.setUserName("john");
        when(userService.getUserByEmail("admin@motech.org")).thenReturn(user);


        persistedUserValidator.validate(new StartupForm(), errors);

        verify(errors).rejectValue("adminEmail", "server.error.email.exist", null, null);
    }

    @Test
    public void shouldRejectOnlyUserIfUserExistsAndIsRegisteredWithIdenticalEmail() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("adminLogin")).thenReturn("admin");
        when(errors.getFieldValue("adminPassword")).thenReturn("password");
        when(errors.getFieldValue("adminConfirmPassword")).thenReturn("password");
        when(errors.getFieldValue("adminEmail")).thenReturn("admin@motech.org");

        when(userService.hasUser("admin")).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setUserName("admin");
        when(userService.getUserByEmail("admin@motech.org")).thenReturn(userDto);

        persistedUserValidator.validate(new StartupForm(), errors);

        verify(errors).rejectValue("adminLogin", "server.error.user.exist", null, null);
        verify(errors, never()).rejectValue("adminEmail", "server.error.email.exist", null, null);
    }
}
