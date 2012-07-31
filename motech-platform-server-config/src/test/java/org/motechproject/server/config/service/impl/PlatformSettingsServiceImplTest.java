package org.motechproject.server.config.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.db.CouchDbManager;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlatformSettingsServiceImplTest {

    @Mock
    ConfigFileSettings motechSettings;

    @Mock
    ResourceLoader resourceLoader;

    @Mock
    CouchDbManager couchDbManager;

    @InjectMocks
    @Spy
    ConfigLoader configLoader = new ConfigLoader(new ArrayList<Resource>());

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

    @Test
    public void testAddConfigLocation() throws IOException {
        String path = "settings.properties";
        ClassPathResource resource = new ClassPathResource(path);
        when(resourceLoader.getResource(path)).thenReturn(resource);

        platformSettingsService.addConfigLocation(path, false);
        ConfigFileSettings configFileSettings = configLoader.loadConfig();

        assertEquals(resource.getURL().getPath(), configFileSettings.getPath());
    }

    @Test
    public void testExport() throws IOException {
        String path = "settings.properties";
        ClassPathResource resource = new ClassPathResource(path);
        when(resourceLoader.getResource(path)).thenReturn(resource);

        platformSettingsService.addConfigLocation(path, false);

        Properties p = platformSettingsService.exportPlatformSettings();

        assertTrue(p.containsKey(MotechSettings.LANGUAGE));
        assertEquals("en", p.getProperty(MotechSettings.LANGUAGE));
        verify(configLoader).loadConfig();
    }
}
