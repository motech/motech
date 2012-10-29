package org.motechproject.commcare.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.commcare.domain.SettingsDto;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.osgi.OsgiFrameworkService;
import org.motechproject.server.osgi.OsgiListener;
import org.osgi.framework.BundleException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OsgiListener.class})
public class SettingsControllerTest {
    private static final String COMMCARE_DOMAIN_KEY = "commcareDomain";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String CASE_EVENT_STRATEGY_KEY = "case.events.send.with.all.data";

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private OsgiFrameworkService osgiFrameworkService;

    private SettingsController controller;

    @Before
    public void setUp() {
        initMocks(this);

        controller = new SettingsController(settingsFacade);
    }

    @Test
    public void testGetSettings() {
        when(settingsFacade.getProperty(COMMCARE_DOMAIN_KEY)).thenReturn("domain");
        when(settingsFacade.getProperty(USERNAME_KEY)).thenReturn("username");
        when(settingsFacade.getProperty(PASSWORD_KEY)).thenReturn("password");
        when(settingsFacade.getProperty(CASE_EVENT_STRATEGY_KEY)).thenReturn("minimal");

        SettingsDto dto = controller.getSettings();

        verify(settingsFacade, times(4)).getProperty(anyString());

        assertEquals("domain", dto.getCommcareDomain());
        assertEquals("username", dto.getUsername());
        assertEquals("password", dto.getPassword());
        assertEquals("minimal", dto.getEventStrategy());
    }

    @Test
    public void testSaveSettings() throws BundleException {
        SettingsDto dto = new SettingsDto();
        dto.setCommcareDomain("http://test.com");
        dto.setUsername("username");
        dto.setPassword("password");
        dto.setEventStrategy("full");

        controller.saveSettings(dto, false);

        verify(settingsFacade, times(4)).setProperty(anyString(), anyString());
    }

    @Test
    public void testSaveSettingsWithRestart() throws BundleException {
        PowerMockito.mockStatic(OsgiListener.class);
        when(OsgiListener.getOsgiService()).thenReturn(osgiFrameworkService);

        SettingsDto dto = new SettingsDto();
        dto.setCommcareDomain("http://test.com");
        dto.setUsername("username");
        dto.setPassword("password");
        dto.setEventStrategy("full");

        controller.saveSettings(dto, true);

        verify(settingsFacade, times(4)).setProperty(anyString(), anyString());
        verify(osgiFrameworkService).restart(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveSettingsEmptyProperty() throws BundleException {
        SettingsDto dto = new SettingsDto();
        dto.setCommcareDomain("http://test.com");
        dto.setPassword("password");
        dto.setEventStrategy("full");

        controller.saveSettings(dto, false);

        verify(settingsFacade, never()).setProperty(anyString(), anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveSettingsInvalidDomainUrl() throws BundleException {
        SettingsDto dto = new SettingsDto();
        dto.setCommcareDomain("test");
        dto.setUsername("username");
        dto.setPassword("password");
        dto.setEventStrategy("full");

        controller.saveSettings(dto, false);

        verify(settingsFacade, never()).setProperty(anyString(), anyString());
    }
}
