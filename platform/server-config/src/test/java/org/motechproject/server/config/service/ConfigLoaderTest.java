package org.motechproject.server.config.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.server.config.domain.SettingsRecord;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigLoaderTest {
    @Mock
    private CoreConfigurationService coreConfigurationService;
    @Spy
    private ConfigLoader configLoader;

    @Before
    public void setUp() {
        configLoader = new ConfigLoader();
        initMocks(this);
        configLoader.setCoreConfigurationService(coreConfigurationService);
        configLoader.setResourceLoader(new DefaultResourceLoader());
    }

    @Test
    public void testMotechSettingsLoading() {
        List<ConfigLocation> configLocations = new ArrayList<>();
        configLocations.add(new ConfigLocation("config/"));
        when(coreConfigurationService.getConfigLocations()).thenReturn(configLocations);

        SettingsRecord settings = configLoader.loadConfig();

        assertNotNull(settings);
        assertEquals(settings.getLanguage(), "en");
    }

    @Test (expected = MotechConfigurationException.class)
    public void testNoFolderExists() {
        List<ConfigLocation> configLocations = new ArrayList<>();
        configLocations.add(new ConfigLocation("config1/"));
        when(coreConfigurationService.getConfigLocations()).thenReturn(configLocations);

        configLoader.loadConfig();
    }

    @Test
    public void testActiveMqPropertiesLoading() {
        List<ConfigLocation> configLocations = new ArrayList<>();
        configLocations.add(new ConfigLocation("config2/"));
        when(coreConfigurationService.getConfigLocations()).thenReturn(configLocations);

        SettingsRecord settings = configLoader.loadConfig();

        assertNotNull(settings);
        assertNotNull(settings.getActivemqProperties().get("jms.notRealActiveMqProperty"));
    }

    @Test
    public void shouldLoadDefaultActiveMq() {
        List<ConfigLocation> configLocations = new ArrayList<>();
        when(coreConfigurationService.getConfigLocations()).thenReturn(configLocations);

        SettingsRecord settings = configLoader.loadDefaultConfig();

        assertNotNull(settings);
        assertEquals(settings.getLanguage(), "en");
        assertNotNull(settings.getActivemqProperties().get("jms.maxConcurrentConsumers"));
    }
}
