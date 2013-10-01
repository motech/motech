package org.motechproject.config.service.impl;

import org.apache.commons.vfs.FileSystemException;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.config.bootstrap.BootstrapConfigManager;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.monitor.ConfigFileMonitor;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigurationServiceTest {
    @Mock
    private BootstrapConfigManager bootstrapConfigManager;

    @Mock
    private ConfigFileMonitor configFileMonitor;

    private ConfigurationService configurationService;

    @Before
    public void setUp() {
        initMocks(this);
        configurationService = new ConfigurationServiceImpl(bootstrapConfigManager, configFileMonitor);
    }

    @Test
    public void shouldLoadBootstrapDBConfiguration() {
        BootstrapConfig expectedConfig = new BootstrapConfig(new DBConfig("http://localhost", null, null), null, null);
        when(bootstrapConfigManager.loadBootstrapConfig()).thenReturn(expectedConfig);

        BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();
        assertNotNull(bootstrapConfig);

        assertThat(bootstrapConfig, IsEqual.equalTo(bootstrapConfig));
    }

    @Test
    public void shouldSaveBootstrapConfig() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://some_url", null, null), "tenentId", ConfigSource.FILE);

        configurationService.save(bootstrapConfig);

        verify(bootstrapConfigManager).saveBootstrapConfig(bootstrapConfig);
    }

    @Test
    public void shouldNotMonitorConfigFilesIfBootstrapConfigIsNotFound() throws FileSystemException {
        when(bootstrapConfigManager.loadBootstrapConfig()).thenReturn(null);

        BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();

        assertNull(bootstrapConfig);
        verify(configFileMonitor, never()).monitor();
    }
}
