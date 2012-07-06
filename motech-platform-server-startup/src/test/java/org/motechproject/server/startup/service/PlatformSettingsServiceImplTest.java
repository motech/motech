package org.motechproject.server.startup.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.startup.service.impl.PlatformSettingsServiceImpl;
import org.motechproject.server.startup.settings.MotechSettings;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlatformSettingsServiceImplTest {

    @Mock
    StartupManager startupManager;

    @Mock
    MotechSettings motechSettings;

    @InjectMocks
    PlatformSettingsService platformSettingsService = new PlatformSettingsServiceImpl();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetLanguage() {
        // no settings
        when(startupManager.getSettings()).thenReturn(null);

        assertNull(platformSettingsService.getPlatformLanguage());
        assertEquals(platformSettingsService.getPlatformLanguage("en"), "en");

        // no language in settings
        when(startupManager.getSettings()).thenReturn(motechSettings);
        when(motechSettings.getLanguage()).thenReturn(null);

        assertEquals(platformSettingsService.getPlatformLanguage(), null);
        assertEquals(platformSettingsService.getPlatformLanguage("en"), "en");

        // language set
        when(motechSettings.getLanguage()).thenReturn("pl");

        assertEquals(platformSettingsService.getPlatformLanguage(), "pl");
        assertEquals(platformSettingsService.getPlatformLanguage("en"), "pl");
    }

    @Test
    public void testGetLocale() {
        // no settings
        when(startupManager.getSettings()).thenReturn(null);

        assertEquals(platformSettingsService.getPlatformLocale(), Locale.getDefault());

        // no language in settings
        when(startupManager.getSettings()).thenReturn(motechSettings);
        when(motechSettings.getLanguage()).thenReturn(null);

        assertEquals(platformSettingsService.getPlatformLocale(), Locale.getDefault());

        // language set
        when(motechSettings.getLanguage()).thenReturn("pl");

        assertEquals(platformSettingsService.getPlatformLocale(), new Locale("pl"));
    }
}
