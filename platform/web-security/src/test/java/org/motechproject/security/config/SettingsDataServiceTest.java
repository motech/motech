package org.motechproject.security.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.validator.impl.MinLengthValidatorDecorator;
import org.motechproject.security.validator.impl.NoneValidator;
import org.motechproject.security.validator.PasswordValidator;
import org.motechproject.security.validator.impl.PasswordValidatorManager;
import org.motechproject.config.domain.MotechSettings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SettingsDataServiceTest {

    @InjectMocks
    private SettingService settingService = new SettingServiceImpl();

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private MotechSettings motechSettings;

    @Mock
    private PasswordValidator validator;

    @Mock
    private NoneValidator noneValidator;

    @Mock
    private PasswordValidatorManager passwordValidatorManager;

    @Before
    public void setUp() {
        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);
        when(passwordValidatorManager.noneValidator()).thenReturn(noneValidator);
    }

    @Test
    public void shouldReturnWhetherEmailIsRequired() {
        when(motechSettings.getEmailRequired()).thenReturn(false);
        assertFalse(settingService.getEmailRequired());

        when(motechSettings.getEmailRequired()).thenReturn(true);
        assertTrue(settingService.getEmailRequired());
    }

    @Test
    public void shouldReturnTheSessionTimeout() {
        when(motechSettings.getSessionTimeout()).thenReturn(500);
        assertEquals(500, settingService.getSessionTimeout());

        // verify the default
        when(motechSettings.getSessionTimeout()).thenReturn(null);
        assertEquals(SettingService.DEFAULT_SESSION_TIMEOUT, settingService.getSessionTimeout());
    }

    @Test
    public void shouldReturnThePasswordValidator() {
        // no setting
        when(motechSettings.getPasswordValidator()).thenReturn("");

        assertEquals(noneValidator, settingService.getPasswordValidator());

        // no validator
        when(motechSettings.getPasswordValidator()).thenReturn("test");
        when(passwordValidatorManager.getValidator("test")).thenReturn(null);

        assertEquals(noneValidator, settingService.getPasswordValidator());

        // validator present
        when(passwordValidatorManager.getValidator("test")).thenReturn(validator);

        assertEquals(validator, settingService.getPasswordValidator());

        // decoration with min pass length
        when(motechSettings.getMinPasswordLength()).thenReturn(10);
        assertTrue(settingService.getPasswordValidator() instanceof MinLengthValidatorDecorator);
    }

    @Test
    public void shouldReturnMinimalPasswordLength() {
        when(motechSettings.getMinPasswordLength()).thenReturn(5);
        assertEquals(5, settingService.getMinPasswordLength());

        when(motechSettings.getMinPasswordLength()).thenReturn(null);
        assertEquals(0, settingService.getMinPasswordLength());
    }

    @Test
    public void shouldReturnPasswordResetSettings() {
        when(motechSettings.getNumberOfDaysToChangePassword()).thenReturn(30);
        assertEquals(30, settingService.getNumberOfDaysToChangePassword());

        when(motechSettings.getNumberOfDaysToChangePassword()).thenReturn(null);
        assertEquals(0, settingService.getNumberOfDaysToChangePassword());

        when(motechSettings.getNumberOfDaysForReminder()).thenReturn(5);
        assertEquals(5, settingService.getNumberOfDaysForReminder());

        when(motechSettings.getNumberOfDaysForReminder()).thenReturn(null);
        assertEquals(0, settingService.getNumberOfDaysForReminder());

        when(motechSettings.isPasswordResetReminderEnabled()).thenReturn(true);
        assertTrue(settingService.isPasswordResetReminderEnabled());

        when(motechSettings.isPasswordResetReminderEnabled()).thenReturn(false);
        assertFalse(settingService.isPasswordResetReminderEnabled());
    }
}
