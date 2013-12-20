package org.motechproject.server.web.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StartupFormValidatorTest {
    private static final String LOGIN_MODE = "loginMode";
    private static final String ADMIN_LOGIN = "adminLogin";
    private static final String ADMIN_PASSWORD = "adminPassword";
    private static final String ADMIN_CONFIRM_PASSWORD = "adminConfirmPassword";
    private static final String ADMIN_EMAIL = "adminEmail";
    private static final String QUEUE_URL = "queueUrl";
    private static final String PROVIDER_NAME = "providerName";
    private static final String PROVIDER_URL = "providerUrl";
    private static final String EMAIL = "motech@gmail.com";
    private static final String LOCALHOST_QUEUE_URL = "tcp://localhost:61616";
    private static final String LANGUAGE = "language";
    private static final String EMPTY = "";

    @Mock
    private Errors errors;

    @Mock
    private MotechUserService userService;


    private StartupFormValidator startupFormValidator;

    @Before
    public void setUp() {
        initMocks(this);
        startupFormValidator = new StartupFormValidatorFactory().getStartupFormValidator(userService);
        when(userService.hasActiveAdminUser()).thenReturn(true);
    }

    @Test
    public void shouldRejectEmptyFields() {
        StartupForm startupForm = new StartupForm();

        when(errors.getFieldValue(LOGIN_MODE)).thenReturn(EMPTY);
        when(errors.getFieldValue(LANGUAGE)).thenReturn(EMPTY);
        when(errors.getFieldValue(QUEUE_URL)).thenReturn(EMPTY);
        when(errors.getFieldValue(ADMIN_LOGIN)).thenReturn(EMPTY);
        when(errors.getFieldValue(ADMIN_PASSWORD)).thenReturn(EMPTY);
        when(errors.getFieldValue(ADMIN_CONFIRM_PASSWORD)).thenReturn(EMPTY);
        when(errors.getFieldValue(ADMIN_EMAIL)).thenReturn(EMAIL);

        startupFormValidator.validate(startupForm, errors);

        verify(errors).rejectValue(LOGIN_MODE, String.format("server.error.required.%s", LOGIN_MODE), null, null);
        verify(errors).rejectValue(LANGUAGE, String.format("server.error.required.%s", LANGUAGE), null, null);
        verify(errors).rejectValue(QUEUE_URL, String.format("server.error.required.%s", QUEUE_URL), null, null);
    }

    @Test
    public void shouldRejectEmptyUserFieldsWhenLoginModeIsRepository() {
        StartupForm startupForm = new StartupForm();
        when(userService.hasActiveAdminUser()).thenReturn(false);
        when(errors.getFieldValue(LOGIN_MODE)).thenReturn("repository");
        when(errors.getFieldValue(LANGUAGE)).thenReturn("en");
        when(errors.getFieldValue(QUEUE_URL)).thenReturn(LOCALHOST_QUEUE_URL);
        when(errors.getFieldValue(ADMIN_LOGIN)).thenReturn(EMPTY);
        when(errors.getFieldValue(ADMIN_PASSWORD)).thenReturn(EMPTY);
        when(errors.getFieldValue(ADMIN_CONFIRM_PASSWORD)).thenReturn(EMPTY);
        when(errors.getFieldValue(ADMIN_EMAIL)).thenReturn(EMPTY);

        startupFormValidator.validate(startupForm, errors);

        verify(errors).rejectValue(ADMIN_LOGIN, String.format("server.error.required.%s", ADMIN_LOGIN), null, null);
        verify(errors).rejectValue(ADMIN_PASSWORD, String.format("server.error.required.%s", ADMIN_PASSWORD), null, null);
        verify(errors).rejectValue(ADMIN_CONFIRM_PASSWORD, String.format("server.error.required.%s", ADMIN_CONFIRM_PASSWORD), null, null);
        verify(errors).rejectValue(ADMIN_EMAIL, "server.error.invalid.email", null, null);
    }

    @Test
    public void shouldRejectEmptyUserFieldsWhenLoginModeIsOpenId() {
        StartupForm startupForm = new StartupForm();

        when(errors.getFieldValue(LOGIN_MODE)).thenReturn("openId");
        when(errors.getFieldValue(LANGUAGE)).thenReturn("en");
        when(errors.getFieldValue(QUEUE_URL)).thenReturn(QUEUE_URL);
        when(errors.getFieldValue(PROVIDER_NAME)).thenReturn(EMPTY);
        when(errors.getFieldValue(PROVIDER_URL)).thenReturn(EMPTY);

        startupFormValidator.validate(startupForm, errors);

        verify(errors).rejectValue(PROVIDER_NAME, String.format("server.error.required.%s", PROVIDER_NAME), null, null);
        verify(errors).rejectValue(PROVIDER_URL, String.format("server.error.required.%s", PROVIDER_URL), null, null);
    }
}
