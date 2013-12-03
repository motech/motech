package org.motechproject.config.monitor.cnf;

import org.apache.commons.vfs.FileSystemException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.monitor.ConfigFileMonitor;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.service.ConfigLoader;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigFileMonitorCnfTest {

    @Mock
    ConfigLoader configLoader;
    @Mock
    CoreConfigurationService coreConfigurationService;
    @Mock
    ConfigurationService configurationService;

    private ConfigFileMonitorCnf cnf = new ConfigFileMonitorCnf();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldCreateConfigFileMonitorBean_WhenConfigSourceIsFile() throws FileSystemException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://some_url", "some_username", "some_password"),
                "tenantId", ConfigSource.FILE);
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);
        ConfigFileMonitor configFileMonitor = cnf.configFileMonitor(configLoader, configurationService, coreConfigurationService);
        assertNotNull(configFileMonitor);
    }

    @Test
    public void shouldNotCreateConfigFileMonitorBean_WhenConfigSourceIsUI() throws FileSystemException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://some_url", "some_username", "some_password"),
                "tenantId", ConfigSource.UI);
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);
        ConfigFileMonitor configFileMonitor = cnf.configFileMonitor(configLoader, configurationService, coreConfigurationService);
        assertNull(configFileMonitor);
    }
}
