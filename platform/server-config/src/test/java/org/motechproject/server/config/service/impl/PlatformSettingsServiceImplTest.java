package org.motechproject.server.config.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.couchdb.service.impl.CouchDbManagerImpl;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.repository.AllSettings;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.domain.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class
    PlatformSettingsServiceImplTest {

    @Mock
    AllSettings allSettings;

    @Mock
    SettingsRecord settingsRecord;

    @Mock
    CouchDbManagerImpl couchDbManager;

    @Mock
    ConfigFileMonitor configFileMonitor;

    @InjectMocks
    PlatformSettingsService platformSettingsService = new PlatformSettingsServiceImpl();

    @Before
    public void setUp() {
        initMocks(this);
        platformSettingsService.evictMotechSettingsCache();
    }

    @Test
    public void testGetLanguage() {
        // no settings
        when(platformSettingsService.getPlatformSettings()).thenReturn(null);
        when(allSettings.getSettings()).thenReturn(settingsRecord);

        assertNull(platformSettingsService.getPlatformLanguage());
        assertEquals(platformSettingsService.getPlatformLanguage("en"), "en");

        // no language in settings
        when(platformSettingsService.getPlatformSettings()).thenReturn(settingsRecord);
        when(settingsRecord.getLanguage()).thenReturn(null);

        assertNull(platformSettingsService.getPlatformLanguage());
        assertEquals(platformSettingsService.getPlatformLanguage("en"), "en");

        // language set
        when(settingsRecord.getLanguage()).thenReturn("pl");

        assertEquals(platformSettingsService.getPlatformLanguage(), "pl");
        assertEquals(platformSettingsService.getPlatformLanguage("en"), "pl");
    }

    @Test
    public void testGetLocale() {
        // no settings
        when(platformSettingsService.getPlatformSettings()).thenReturn(null);
        when(allSettings.getSettings()).thenReturn(null);

        assertEquals(platformSettingsService.getPlatformLocale(), Locale.getDefault());

        // no language in settings
        when(platformSettingsService.getPlatformSettings()).thenReturn(settingsRecord);
        when(settingsRecord.getLanguage()).thenReturn(null);

        assertEquals(platformSettingsService.getPlatformLocale(), Locale.getDefault());

        // language set
        when(settingsRecord.getLanguage()).thenReturn("pl");

        assertEquals(platformSettingsService.getPlatformLocale(), new Locale("pl"));
    }

    @Test
    public void testExport() throws IOException {
        ConfigFileSettings configFileSettings = new ConfigFileSettings();
        configFileSettings.saveMotechSetting(MotechSettings.LANGUAGE, "en");
        when(configFileMonitor.getCurrentSettings()).thenReturn(configFileSettings);

        Properties p = platformSettingsService.exportPlatformSettings();

        assertTrue(p.containsKey(MotechSettings.LANGUAGE));
        assertEquals(configFileSettings.getLanguage(), p.getProperty(MotechSettings.LANGUAGE));
    }
}
