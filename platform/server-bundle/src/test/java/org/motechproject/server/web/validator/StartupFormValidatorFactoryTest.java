package org.motechproject.server.web.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Validator;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class StartupFormValidatorFactoryTest {

    @InjectMocks
    private StartupFormValidatorFactory startupFormValidatorFactory;

    @Mock
    private MotechUserService motechUserService;

    @Mock
    private ConfigurationService configurationService;

    @Before
    public void before() {
        startupFormValidatorFactory = new StartupFormValidatorFactory();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateStartupFormValidator() {
        when(motechUserService.hasActiveAdminUser()).thenReturn(false);

        StartupFormValidator startupFormValidator = startupFormValidatorFactory.getStartupFormValidator(motechUserService);

        assertNotNull(startupFormValidator);
        List<Validator> validators = startupFormValidator.getValidators();
        assertFalse(validators.isEmpty());
        assertTrue(validators.contains(new RequiredFieldValidator(StartupForm.LANGUAGE)));
        assertTrue(validators.contains(new RequiredFieldValidator(StartupForm.LOGIN_MODE)));
        assertContainsValidatorOfType(QueueURLValidator.class, validators);
        assertContainsValidatorOfType(UserRegistrationValidator.class, validators);
    }

    private void assertContainsValidatorOfType(Class clazz, List<Validator> validators) {
        for (Validator validator : validators) {
            if (validator.getClass().equals(clazz)) {
                return;
            }
        }
        fail(String.format("List of validators for startup does not contain validator of type %s", clazz));
    }
}
