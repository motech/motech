package org.motechproject.server.config.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
    public void shouldLoadSupportedFilesFromGivenConfigLocationAndProcessThem() throws IOException {
        final String dirPath = this.getClass().getClassLoader().getResource("config").getFile();
        final List<File> expectedFiles = Arrays.asList(
                new File(dirPath, "org.motechproject.motech-module1/somemodule.properties"),
                new File(dirPath, "org.motechproject.motech-module2/somemodule.json")
        );
        final ConfigLocation configLocation = new ConfigLocation(dirPath);
        when(coreConfigurationService.getConfigLocation()).thenReturn(configLocation);

        configLoader.processExistingConfigs();

        verify(configurationService).addOrUpdate(filesCaptor.capture());
        List<File> actualFiles = filesCaptor.getValue();
        assertEquals(expectedFiles, actualFiles);
    }
}
