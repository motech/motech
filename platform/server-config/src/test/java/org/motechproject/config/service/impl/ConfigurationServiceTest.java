package org.motechproject.config.service.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.config.bootstrap.BootstrapConfigManager;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigurationServiceTest {
    @Mock
    private BootstrapConfigManager bootstrapConfigManager;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldLoadBootstrapDBConfiguration() {
        ConfigurationService platformConfigService = new ConfigurationServiceImpl(bootstrapConfigManager);
        BootstrapConfig expectedConfig = new BootstrapConfig(new DBConfig("http://localhost", null, null), null, null);
        when(bootstrapConfigManager.loadBootstrapConfig()).thenReturn(expectedConfig);

        BootstrapConfig bootstrapConfig = platformConfigService.loadBootstrapConfig();
        assertNotNull(bootstrapConfig);

        assertThat(bootstrapConfig, IsEqual.equalTo(bootstrapConfig));
    }

    @Test
    public void shouldSaveBootstrapConfig() throws IOException {
        ConfigurationServiceImpl configurationService = new ConfigurationServiceImpl(bootstrapConfigManager);
                BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://some_url", null, null), "tenentId", ConfigSource.FILE);

        configurationService.save(bootstrapConfig);

        verify(bootstrapConfigManager).saveBootstrapConfig(bootstrapConfig);
    }
}
