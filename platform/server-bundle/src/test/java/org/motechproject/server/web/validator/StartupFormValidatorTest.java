package org.motechproject.server.web.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.security.helper.AuthenticationMode;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.startup.StartupManager;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.Errors;
import org.motechproject.server.web.form.StartupForm;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;

/*StartupFormValidatorTest class provides tests for StartupFormValidator*/
@RunWith(PowerMockRunner.class)
public class StartupFormValidatorTest {

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
        when(errors.getFieldValue("loginMode")).thenReturn(AuthenticationMode.REPOSITORY);
        when(userService.hasUser("motech")).thenReturn(true);
        when(errors.getFieldValue("adminLogin")).thenReturn("motech");
        when(errors.getFieldValue("adminPassword")).thenReturn("password001");
        when(errors.getFieldValue("adminConfirmPassword")).thenReturn("password001");
        when(errors.getFieldValue("adminEmail")).thenReturn("motech@gmail.com");
        when(errors.getFieldErrorCount("motech")).thenReturn(0);
        when(errors.getFieldValue("queueUrl")).thenReturn("localhost");
        startupFormValidator.validate(startupForm, errors);
        verify(errors).rejectValue("adminLogin", "server.error.user.exist", null, null);

    }
}
