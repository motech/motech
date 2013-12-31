package org.motechproject.server.web.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.web.form.StartupForm;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.Errors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/*StartupFormValidatorTest class provides tests for StartupFormValidator*/
@RunWith(PowerMockRunner.class)
public class StartupFormValidatorTest {
    private static final String LOGIN_MODE = "loginMode";
    private static final String ADMIN_LOGIN = "adminLogin";
    private static final String ADMIN_PASSWORD = "adminPassword";
    private static final String ADMIN_CONFIRM_PASSWORD = "adminConfirmPassword";
    private static final String ADMIN_EMAIL = "adminEmail";
    private static final String QUEUE_URL = "queueUrl";
    private static final String PROVIDER_NAME = "providerName";
    private static final String PROVIDER_URL = "providerUrl";
    private static final String LANGUAGE = "language";
    private static final String LOGIN = "motech";
    private static final String LOGIN2 = "motech2";
    private static final String PASSWORD = "password001";
    private static final String EMAIL = "motech@gmail.com";
    private static final String LOCALHOST = "tcp://localhost:61616";
    private static final String USER_EXIST = "server.error.user.exist";
    private static final String EMAIL_EXIST = "server.error.email.exist";

    @Mock
    private Errors errors;

    @Mock
    private MotechUserService userService;

    @InjectMocks
    private StartupFormValidator startupFormValidator = new StartupFormValidator(userService);

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testUserExistence() {
        StartupForm startupForm = new StartupForm();

        when(errors.getFieldValue(LOGIN_MODE)).thenReturn(LoginMode.REPOSITORY.getName());
        when(userService.hasUser(LOGIN)).thenReturn(true);
        when(errors.getFieldValue(ADMIN_LOGIN)).thenReturn(LOGIN);
        when(errors.getFieldValue(ADMIN_PASSWORD)).thenReturn(PASSWORD);
        when(errors.getFieldValue(ADMIN_CONFIRM_PASSWORD)).thenReturn(PASSWORD);
        when(errors.getFieldValue(ADMIN_EMAIL)).thenReturn(EMAIL);
        when(errors.getFieldErrorCount(LOGIN)).thenReturn(0);
        when(errors.getFieldValue(QUEUE_URL)).thenReturn(LOCALHOST);

        startupFormValidator.validate(startupForm, errors);
        verify(errors).rejectValue(ADMIN_LOGIN, USER_EXIST, null, null);

    }

    @Test
    public void testEmailExistence() {
        StartupForm startupForm = new StartupForm();
        UserDto user = new UserDto();
        user.setUserName(LOGIN2);

        when(errors.getFieldValue(LOGIN_MODE)).thenReturn(LoginMode.REPOSITORY.getName());
        when(userService.hasUser(LOGIN)).thenReturn(false);
        when(errors.getFieldValue(ADMIN_LOGIN)).thenReturn(LOGIN);
        when(errors.getFieldValue(ADMIN_PASSWORD)).thenReturn(PASSWORD);
        when(errors.getFieldValue(ADMIN_CONFIRM_PASSWORD)).thenReturn(PASSWORD);
        when(errors.getFieldValue(ADMIN_EMAIL)).thenReturn(EMAIL);
        when(errors.getFieldErrorCount(LOGIN)).thenReturn(0);
        when(errors.getFieldValue(QUEUE_URL)).thenReturn(LOCALHOST);
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);

        startupFormValidator.validate(startupForm, errors);
        verify(errors).rejectValue(ADMIN_EMAIL, EMAIL_EXIST, null, null);
    }

    @Test
    public void shouldValidateOpenIdDetails() {
        StartupForm form = new StartupForm();

        when(errors.getFieldValue(LOGIN_MODE)).thenReturn(LoginMode.OPEN_ID.getName());
        when(errors.getFieldValue(ADMIN_LOGIN)).thenReturn("");
        when(errors.getFieldValue(ADMIN_PASSWORD)).thenReturn(null);
        when(errors.getFieldValue(ADMIN_CONFIRM_PASSWORD)).thenReturn("");
        when(errors.getFieldValue(ADMIN_EMAIL)).thenReturn("");
        when(errors.getFieldErrorCount(LOGIN)).thenReturn(1);
        when(errors.getFieldValue(QUEUE_URL)).thenReturn(LOCALHOST);
        when(errors.getFieldValue(PROVIDER_NAME)).thenReturn("Google");
        when(errors.getFieldValue(PROVIDER_URL)).thenReturn("https://www.google.com/accounts/o8/id");
        when(errors.getFieldValue(LANGUAGE)).thenReturn("en");

        startupFormValidator.validate(form, errors);
        verifyNoRejections();

        when(errors.getFieldValue(PROVIDER_URL)).thenReturn("");

        startupFormValidator.validate(form, errors);
        verify(errors).rejectValue(PROVIDER_URL, "server.error.required.providerUrl", null, null);

        when(errors.getFieldValue(PROVIDER_URL)).thenReturn("https://www.google.com/accounts/o8/id");
        when(errors.getFieldValue(PROVIDER_NAME)).thenReturn("");

        startupFormValidator.validate(form, errors);
        verify(errors).rejectValue(PROVIDER_NAME, "server.error.required.providerName", null, null);
    }

    private void verifyNoRejections() {
        verify(errors, never()).rejectValue(anyString(), anyString());
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
        verify(errors, never()).rejectValue(anyString(), anyString(), any(Object[].class), anyString());
    }
}
