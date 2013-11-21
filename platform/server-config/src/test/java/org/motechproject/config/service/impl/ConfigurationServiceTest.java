package org.motechproject.config.service.impl;

import org.apache.commons.vfs.FileSystemException;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.domain.ModulePropertiesRecord;
import org.motechproject.config.repository.AllModuleProperties;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.monitor.ConfigFileMonitor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
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
    AllModuleProperties allModuleProperties;

    @Captor
    ArgumentCaptor<List<ModulePropertiesRecord>> propertiesCaptor;

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
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(null);

        BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();

        assertNull(bootstrapConfig);
        verify(configFileMonitor, never()).monitor();
    }

    @Test
    public void shouldBulkAddOrUpdateProperties() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file1 = new File(classLoader.getResource("config/org.motechproject.motech-module1/somemodule.properties").getPath());
        File file2 = new File(classLoader.getResource("config/org.motechproject.motech-module2/somemodule.json").getPath());

        configurationService.addOrUpdate(Arrays.asList(file1, file2));

        verify(allModuleProperties).bulkAddOrUpdate(propertiesCaptor.capture());
        List<ModulePropertiesRecord> actualRecords = propertiesCaptor.getValue();
        assertEquals(2, actualRecords.size());
        assertEquals("somemodule.properties", actualRecords.get(0).getFilename());
        assertEquals("somemodule.json", actualRecords.get(1).getFilename());
    }
}
