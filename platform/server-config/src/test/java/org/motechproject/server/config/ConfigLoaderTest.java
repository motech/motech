package org.motechproject.server.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.config.domain.ConfigLocation;
import org.motechproject.config.filestore.ConfigLocationFileStore;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigLoaderTest {
    @Mock
    private ConfigLocationFileStore configLocationFileStore;
    @Spy
    private ConfigLoader configLoader;

    @Before
    public void setUp() {
        configLoader = new ConfigLoader();
        initMocks(this);
        configLoader.setConfigLocationFileStore(configLocationFileStore);
        configLoader.setResourceLoader(new DefaultResourceLoader());
    }

    @Test
    public void testMotechSettingsLoading() {
        List<ConfigLocation> configLocations = new ArrayList<>();
        configLocations.add(new ConfigLocation("config/"));
        when(configLocationFileStore.getAll()).thenReturn(configLocations);

        ConfigFileSettings settings = configLoader.loadConfig();

        assertNotNull(settings);
        assertEquals(settings.getLanguage(), "en");
    }

    @Test
    public void testNoFolderExists() {
        List<ConfigLocation> configLocations = new ArrayList<>();
        configLocations.add(new ConfigLocation("config1/"));
        when(configLocationFileStore.getAll()).thenReturn(configLocations);

        ConfigFileSettings settings = configLoader.loadConfig();

        assertNull(settings);
    }

    @Test
    public void testActiveMqPropertiesLoading() {
        List<ConfigLocation> configLocations = new ArrayList<>();
        configLocations.add(new ConfigLocation("config2/"));
        when(configLocationFileStore.getAll()).thenReturn(configLocations);

        ConfigFileSettings settings = configLoader.loadConfig();

        assertNotNull(settings);
        assertNotNull(settings.getActivemqProperties().get("notRealActiveMqProperty"));
    }

    @Test
    public void shouldLoadDefaultActiveMq() {
        List<ConfigLocation> configLocations = new ArrayList<>();
        when(configLocationFileStore.getAll()).thenReturn(configLocations);

        ConfigFileSettings settings = configLoader.loadDefaultConfig();

        assertNotNull(settings);
        assertEquals(settings.getLanguage(), "en");
        assertNotNull(settings.getActivemqProperties().get("maxConcurrentConsumers"));
    }
}
