package org.motechproject.server.config.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.SettingsRecord;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigLoaderTest {
    @Mock
    private CoreConfigurationService coreConfigurationService;
    @Mock
    private ConfigurationService configurationService;
    @Spy
    @InjectMocks
    private ConfigLoader configLoader = new ConfigLoader();
    @Captor
    private ArgumentCaptor<List<File>> filesCaptor;

    @Before
    public void setUp() {
        initMocks(this);
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

    @Test(expected = MotechConfigurationException.class)
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

    @Test
    public void shouldLoadSupportedFilesFromGivenConfigLocationAndProcessThem() throws IOException {
        final String dirPath = this.getClass().getClassLoader().getResource("config").getFile();
        final List<File> expectedFiles = Arrays.asList(
                new File(dirPath, "somemodule.json"),
                new File(dirPath, "somemodule.properties")
        );
        final ConfigLocation configLocation = new ConfigLocation(dirPath);
        configLocation.markAsCurrentLocation();
        when(coreConfigurationService.getConfigLocations()).thenReturn(Arrays.asList(configLocation));

        configLoader.processExistingConfigs();

        verify(configurationService).addOrUpdateProperties(filesCaptor.capture());
        List<File> actualFiles = filesCaptor.getValue();
        assertEquals(expectedFiles, actualFiles);
    }
}
