package org.motechproject.server.web.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.domain.LoginMode;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.Errors;
import org.motechproject.server.web.form.StartupForm;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;

/*StartupFormValidatorTest class provides tests for StartupFormValidator*/
@RunWith(PowerMockRunner.class)
public class StartupFormValidatorTest {
    private static final String LOGIN_MODE = "loginMode";
    private static final String ADMIN_LOGIN = "adminLogin";
    private static final String ADMIN_PASSWORD = "adminPassword";
    private static final String ADMIN_CONFIRM_PASSWORD = "adminConfirmPassword";
    private static final String ADMIN_EMAIL = "adminEmail";
    private static final String QUEUE_URL = "queueUrl";
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
}
