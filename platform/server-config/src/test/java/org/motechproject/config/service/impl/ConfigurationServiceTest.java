package org.motechproject.config.service.impl;

import org.apache.commons.vfs.FileSystemException;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.repository.AllModuleProperties;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.monitor.ConfigFileMonitor;

import java.io.IOException;
import java.util.Properties;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigurationServiceTest {
    @Mock
    private CoreConfigurationService coreConfigurationService;

    @Mock
    private ConfigFileMonitor configFileMonitor;

    @Mock
    private AllModuleProperties allModuleProperties;

    @InjectMocks
    private ConfigurationService configurationService = new ConfigurationServiceImpl();

    @Before
    public void setUp() {
        initMocks(this);
        configurationService.evictMotechSettingsCache();
    }

    @Test
    public void shouldLoadBootstrapDBConfiguration() {
        BootstrapConfig expectedConfig = new BootstrapConfig(new DBConfig("http://localhost", null, null), null, null);
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(expectedConfig);

        BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();
        assertNotNull(bootstrapConfig);

        assertThat(bootstrapConfig, IsEqual.equalTo(bootstrapConfig));
    }

    @Test
    public void shouldSaveBootstrapConfig() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://some_url", null, null), "tenentId", ConfigSource.FILE);

        configurationService.save(bootstrapConfig);

        verify(coreConfigurationService).saveBootstrapConfig(bootstrapConfig);
    }

    @Test
    public void shouldNotMonitorConfigFilesIfBootstrapConfigIsNotFound() throws FileSystemException {
        when(coreConfigurationService.loadBootstrapConfig()).thenThrow(new MotechConfigurationException("File not found"));

        BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();

        assertNull(bootstrapConfig);
        verify(configFileMonitor, never()).monitor();
    }

    @Test
    public void shouldGetModuleProperties() throws java.io.IOException {
        final Properties defaultProperties = new Properties();
        defaultProperties.put("apiKey", "123");
        defaultProperties.put("port", "8000");
        final String module = "mds";
        final String filename = "filename";
        final Properties overriddenProperties = new Properties();
        overriddenProperties.put("port", "4000");
        when(allModuleProperties.asProperties(module, filename)).thenReturn(overriddenProperties);

        final Properties moduleProperties = configurationService.getModuleProperties(module, filename, defaultProperties);

        assertThat(moduleProperties.getProperty("apiKey"), IsEqual.equalTo("123"));
        assertThat(moduleProperties.getProperty("port"), IsEqual.equalTo("4000"));
    }

    @Test
    public void shouldGetEmptyPropertiesWhenNoPropertiesAreFound() throws java.io.IOException {
        final String module = "mds";
        final String filename = "filename";
        when(allModuleProperties.asProperties(module, filename)).thenReturn(null);

        final Properties moduleProperties = configurationService.getModuleProperties(module, filename, null);
        assertNotNull(moduleProperties);
    }
}
