package org.motechproject.server.config.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlatformSettingsServiceImplTest {

    @Mock
    ConfigFileSettings motechSettings;

    @Mock
    ConfigLoader configLoader;

    @InjectMocks
    PlatformSettingsService platformSettingsService = new PlatformSettingsServiceImpl();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetLanguage() {
        // no settings
        when(platformSettingsService.getPlatformSettings()).thenReturn(null);

        assertNull(platformSettingsService.getPlatformLanguage());
        assertEquals(platformSettingsService.getPlatformLanguage("en"), "en");

        // no language in settings
        when(platformSettingsService.getPlatformSettings()).thenReturn(motechSettings);
        when(motechSettings.getLanguage()).thenReturn(null);

        assertNull(platformSettingsService.getPlatformLanguage());
        assertEquals(platformSettingsService.getPlatformLanguage("en"), "en");

        // language set
        when(motechSettings.getLanguage()).thenReturn("pl");

        assertEquals(platformSettingsService.getPlatformLanguage(), "pl");
        assertEquals(platformSettingsService.getPlatformLanguage("en"), "pl");
    }

    @Test
    public void testGetLocale() {
        // no settings
        when(platformSettingsService.getPlatformSettings()).thenReturn(null);

        assertEquals(platformSettingsService.getPlatformLocale(), Locale.getDefault());

        // no language in settings
        when(platformSettingsService.getPlatformSettings()).thenReturn(motechSettings);
        when(motechSettings.getLanguage()).thenReturn(null);

        assertEquals(platformSettingsService.getPlatformLocale(), Locale.getDefault());

        // language set
        when(motechSettings.getLanguage()).thenReturn("pl");

        assertEquals(platformSettingsService.getPlatformLocale(), new Locale("pl"));
    }
}
