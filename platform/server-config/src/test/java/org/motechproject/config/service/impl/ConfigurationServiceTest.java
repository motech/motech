package org.motechproject.config.service.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.bootstrap.BootstrapConfigLoader;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigurationServiceTest {
    @Mock
    private BootstrapConfigLoader bootstrapConfigLoader;

    @InjectMocks
    private ConfigurationService platformConfigService = new ConfigurationServiceImpl();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldLoadBootstrapDBConfiguration() {
        BootstrapConfig expectedConfig = new BootstrapConfig(new DBConfig("http://localhost", null, null), null, null);
        when(bootstrapConfigLoader.loadBootstrapConfig()).thenReturn(expectedConfig);

        BootstrapConfig bootstrapConfig = platformConfigService.loadBootstrapConfig();
        assertNotNull(bootstrapConfig);

        assertThat(bootstrapConfig, IsEqual.equalTo(bootstrapConfig));
    }
}
