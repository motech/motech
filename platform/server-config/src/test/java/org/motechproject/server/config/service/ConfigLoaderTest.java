package org.motechproject.server.config.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.SettingsRecord;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigLoaderTest {
    @Mock
    private CoreConfigurationService coreConfigurationService;
    @Mock
    private ConfigurationService configurationService;
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
        ConfigLocation configLocation = new ConfigLocation("config/");
        when(coreConfigurationService.getConfigLocation()).thenReturn(configLocation);

        SettingsRecord settings = configLoader.loadMotechSettings();

        assertNotNull(settings);
        assertEquals(settings.getLanguage(), "en");
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfErrorReadingSettingsFile() throws MalformedURLException {
        ConfigLocation configLocation = mock(ConfigLocation.class);
        UrlResource resource = mock(UrlResource.class);
        when(coreConfigurationService.getConfigLocation()).thenReturn(configLocation);
        when(configLocation.toResource()).thenReturn(resource);
        when(resource.createRelative(ConfigurationConstants.SETTINGS_FILE_NAME)).thenThrow(new MalformedURLException());

        configLoader.loadMotechSettings();
    }

    @Test
    public void testActiveMqPropertiesLoading() {
        ConfigLocation configLocation = new ConfigLocation("config2/");
        when(coreConfigurationService.getConfigLocation()).thenReturn(configLocation);

        SettingsRecord settings = configLoader.loadMotechSettings();

        assertNotNull(settings);
        assertNotNull(settings.getActivemqProperties().get("jms.notRealActiveMqProperty"));
    }

    @Test
    public void shouldLoadDefaultActiveMq() {
        SettingsRecord settings = configLoader.loadDefaultConfig();

        assertNotNull(settings);
        assertEquals(settings.getLanguage(), "en");
        assertNotNull(settings.getActivemqProperties().get("jms.maxConcurrentConsumers"));
    }

    @Test
    public void shouldLoadSupportedFilesFromGivenConfigLocation() throws IOException {
        final String dirPath = this.getClass().getClassLoader().getResource("config").getFile();
        final File file1 = new File(dirPath, "motech-settings.properties");
        final File file2 = new File(dirPath, "org.motechproject.motech-module1/somemodule.properties");
        final File file3 = new File(dirPath, "org.motechproject.motech-module2/somemodule.json");
        final List<File> expectedFiles = Arrays.asList(file1, file2, file3);

        final ConfigLocation configLocation = new ConfigLocation(dirPath);
        when(coreConfigurationService.getConfigLocation()).thenReturn(configLocation);

        final List<File> actualFiles = configLoader.findExistingConfigs();

        assertEquals(expectedFiles.size(), actualFiles.size());
        assertTrue(actualFiles.containsAll(expectedFiles));
    }

    @Test
    public void shouldReturnFilesInConfigLocation() throws IOException {
        ConfigLocation configLocation = mock(ConfigLocation.class);
        File file = mock(File.class);
        List<File> files = Arrays.asList(file);
        when(configLocation.getExistingConfigFiles()).thenReturn(files);
        when(coreConfigurationService.getConfigLocation()).thenReturn(configLocation);
        List<File> existingConfigs = configLoader.findExistingConfigs();
        assertSame(files, existingConfigs);
    }
}
