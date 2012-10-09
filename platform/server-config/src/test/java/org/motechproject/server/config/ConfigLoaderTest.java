package org.motechproject.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class ConfigLoaderTest {

    @Test
    public void testMotechSettingsLoading() {
        List<Resource> configLocations = new ArrayList<>();
        configLocations.add(new ClassPathResource("config/"));
        ConfigLoader configLoader = new ConfigLoader(configLocations);
        configLoader.setResourceLoader(new DefaultResourceLoader());

        ConfigFileSettings settings = configLoader.loadConfig();
        assertNotNull(settings);
        assertEquals(settings.getLanguage(), "en");
    }

    @Test
    public void testNoFolderExists() {
        List<Resource> configLocations = new ArrayList<>();
        configLocations.add(new ClassPathResource("config1/"));
        ConfigLoader configLoader = new ConfigLoader(configLocations);

        ConfigFileSettings settings = configLoader.loadConfig();
        assertNull(settings);
    }

    @Test
    public void testActiveMqPropertiesLoading() {
        List<Resource> configLocations = new ArrayList<>();
        configLocations.add(new ClassPathResource("config2/"));
        ConfigLoader configLoader = new ConfigLoader(configLocations);

        ConfigFileSettings settings = configLoader.loadConfig();
        assertNotNull(settings);
        assertNotNull(settings.getActivemqProperties().get("notRealActiveMqProperty"));
    }

    @Test
    public void shouldLoadDefaultActiveMq() {
        List<Resource> configLocations = new ArrayList<>();
        ConfigLoader configLoader = new ConfigLoader(configLocations);
        configLoader.setResourceLoader(new DefaultResourceLoader());

        ConfigFileSettings settings = configLoader.loadDefaultConfig();
        assertNotNull(settings);
        assertEquals(settings.getLanguage(), "en");
        assertNotNull(settings.getActivemqProperties().get("maxConcurrentConsumers"));
    }
}
