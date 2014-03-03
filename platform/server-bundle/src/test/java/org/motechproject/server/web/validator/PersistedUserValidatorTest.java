package org.motechproject.server.web.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.form.StartupForm;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

        List<String> errors = new ArrayList<>();
        persistedUserValidator.validate(new StartupForm(), errors, ConfigSource.FILE);

        assertTrue(errors.contains("server.error.required.adminLogin"));
        assertTrue(errors.contains("server.error.required.adminPassword"));
        assertTrue(errors.contains("server.error.invalid.email"));
        assertFalse(errors.contains("server.error.invalid.password"));
    }

    @Test
    public void shouldRejectInvalidEmail() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        StartupForm startupForm = getExampleStartupForm();
        startupForm.setAdminEmail("admin@motech");

        when(userService.hasUser("admin")).thenReturn(false);

        List<String> errors = new ArrayList<>();
        persistedUserValidator.validate(startupForm, errors, ConfigSource.FILE);

        assertTrue(errors.contains("server.error.invalid.email"));
    }

    @Test
    public void shouldRejectPasswordIfConfirmPasswordValueIsDifferent() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        StartupForm startupForm = getExampleStartupForm();
        startupForm.setAdminConfirmPassword("Password");

        when(userService.hasUser("admin")).thenReturn(false);

        List<String> errors = new ArrayList<>();
        persistedUserValidator.validate(startupForm, errors, ConfigSource.FILE);

        //If password is empty do not check against confirmPassword as empty password error is already added
        assertTrue(errors.contains("server.error.invalid.password"));
    }

    @Test
    public void shouldRejectUserIfUserExists() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        when(userService.hasUser("admin")).thenReturn(true);

        List<String> errors = new ArrayList<>();
        persistedUserValidator.validate(getExampleStartupForm(), errors, ConfigSource.FILE);

        assertTrue(errors.contains("server.error.user.exist"));
    }

    @Test
    public void shouldRejectEmailIfInUse() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        when(userService.hasUser("admin")).thenReturn(false);
        UserDto user = new UserDto();
        user.setUserName("john");
        when(userService.getUserByEmail("admin@motech.org")).thenReturn(user);

        List<String> errors = new ArrayList<>();
        persistedUserValidator.validate(getExampleStartupForm(), errors, ConfigSource.FILE);

        assertTrue(errors.contains("server.error.email.exist"));
    }

    @Test
    public void shouldRejectOnlyUserIfUserExistsAndIsRegisteredWithIdenticalEmail() {
        PersistedUserValidator persistedUserValidator = new PersistedUserValidator(userService);

        when(userService.hasUser("admin")).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setUserName("admin");
        when(userService.getUserByEmail("admin@motech.org")).thenReturn(userDto);

        List<String> errors = new ArrayList<>();
        persistedUserValidator.validate(getExampleStartupForm(), errors, ConfigSource.FILE);

        assertTrue(errors.contains("server.error.user.exist"));
        assertFalse(errors.contains("server.error.email.exist"));
    }

    private StartupForm getExampleStartupForm() {
        StartupForm startupForm = new StartupForm();
        startupForm.setAdminLogin("admin");
        startupForm.setAdminPassword("password");
        startupForm.setAdminConfirmPassword("password");
        startupForm.setAdminEmail("admin@motech.org");

        return startupForm;
    }
}
