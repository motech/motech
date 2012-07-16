package org.motechproject.server.config;

import org.junit.Test;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigLoaderTest {

    @Test
    public void testConfigLoading() {
        List<Resource> configLocations = new ArrayList<>();
        configLocations.add(new ClassPathResource("settings.properties"));
        ConfigLoader configLoader = new ConfigLoader(configLocations);

        ConfigFileSettings settings = configLoader.loadConfig();
        assertNotNull(settings);
        assertEquals(settings.getLanguage(), "en");
    }
}
