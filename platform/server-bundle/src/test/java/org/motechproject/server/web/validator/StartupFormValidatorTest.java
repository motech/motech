package org.motechproject.server.web.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.form.StartupForm;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StartupFormValidatorTest {
    private static final String LOGIN_MODE = "loginMode";
    private static final String ADMIN_LOGIN = "adminLogin";
    private static final String ADMIN_PASSWORD = "adminPassword";
    private static final String ADMIN_CONFIRM_PASSWORD = "adminConfirmPassword";
    private static final String QUEUE_URL = "queueUrl";
    private static final String PROVIDER_NAME = "providerName";
    private static final String PROVIDER_URL = "providerUrl";
    private static final String LOCALHOST_QUEUE_URL = "tcp://localhost:61616";
    private static final String LANGUAGE = "language";
    private static final String EMAIL = "motech@gmail.com";

    @Mock
    private MotechUserService userService;

    private StartupFormValidator startupFormValidator;

    @Before
    public void setUp() {
        initMocks(this);
        when(userService.hasActiveAdminUser()).thenReturn(true);
    }

    @Test
    public void shouldRejectEmptyFields() {
        StartupForm startupForm = new StartupForm();
        startupForm.setAdminEmail(EMAIL);

        startupFormValidator = new StartupFormValidatorFactory().getStartupFormValidator(startupForm, userService);
        List<String> errors = startupFormValidator.validate(startupForm, ConfigSource.UI);

        assertTrue(errors.contains(String.format("server.error.required.%s", LOGIN_MODE)));
        assertTrue(errors.contains(String.format("server.error.required.%s", LANGUAGE)));
        assertTrue(errors.contains(String.format("server.error.required.%s", QUEUE_URL)));
    }

    @Test
    public void shouldRejectEmptyUserFieldsWhenLoginModeIsRepository() {
        StartupForm startupForm = new StartupForm();
        when(userService.hasActiveAdminUser()).thenReturn(false);
        startupForm.setLoginMode("repository");
        startupForm.setLanguage("en");
        startupForm.setQueueUrl(LOCALHOST_QUEUE_URL);

        startupFormValidator = new StartupFormValidatorFactory().getStartupFormValidator(startupForm, userService);
        List<String> errors = startupFormValidator.validate(startupForm, ConfigSource.FILE);

        assertTrue(errors.contains(String.format("server.error.required.%s", ADMIN_LOGIN)));
        assertTrue(errors.contains(String.format("server.error.required.%s", ADMIN_PASSWORD)));
        assertTrue(errors.contains("server.error.invalid.email"));
    }

    @Test
    public void shouldRejectEmptyUserFieldsWhenLoginModeIsOpenId() {
        StartupForm startupForm = new StartupForm();
        startupForm.setLoginMode("openId");
        startupForm.setLanguage("en");
        startupForm.setQueueUrl(QUEUE_URL);

        startupFormValidator = new StartupFormValidatorFactory().getStartupFormValidator(startupForm, userService);
        List<String> errors = startupFormValidator.validate(startupForm, ConfigSource.UI);

        assertTrue(errors.contains(String.format("server.error.required.%s", PROVIDER_NAME)));
        assertTrue(errors.contains(String.format("server.error.required.%s", PROVIDER_URL)));
    }

    @Test
    public void shouldAcceptEmptyQueueURLAndLoginModeWhenConfigSourceIsFile() {
        StartupForm startupForm = new StartupForm();
        startupForm.setAdminLogin(ADMIN_LOGIN);
        startupForm.setAdminPassword(ADMIN_PASSWORD);
        startupForm.setAdminConfirmPassword(ADMIN_CONFIRM_PASSWORD);
        startupForm.setAdminEmail(EMAIL);

        startupFormValidator = new StartupFormValidatorFactory().getStartupFormValidator(startupForm, userService);
        List<String> errors = startupFormValidator.validate(startupForm, ConfigSource.FILE);

        assertTrue(errors.isEmpty());
    }
}
