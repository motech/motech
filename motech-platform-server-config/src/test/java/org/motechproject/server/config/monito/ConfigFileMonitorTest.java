package org.motechproject.server.config.monito;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.scheduler.gateway.MotechSchedulerGateway;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigFileMonitorTest {

    @Mock
    MotechSchedulerGateway schedulerGateway;

    @Mock
    ConfigLoader configLoader;

    @Mock
    PlatformSettingsService platformSettingsService;

    @Mock
    ResourceLoader resourceLoader;

    @Mock
    FileChangeEvent fileChangeEvent;

    @Mock
    ConfigFileSettings motechSettings;

    @Mock
    StandardFileSystemManager systemManager;

    @InjectMocks
    @Spy
    ConfigFileMonitor configFileMonitor = ConfigFileMonitor.getInstance();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        configFileMonitor.setSchedulerGateway(schedulerGateway);
        configFileMonitor.setConfigLoader(configLoader);
        configFileMonitor.setPlatformSettingsService(platformSettingsService);
        configFileMonitor.setSystemManager(systemManager);

        configFileMonitor.afterPropertiesSet();
    }

    @Test
    public void testChangeConfigFileLocation() throws Exception {
        String path = "settings.properties";
        ClassPathResource resource = new ClassPathResource(path);
        when(resourceLoader.getResource(path)).thenReturn(resource);

        configFileMonitor.changeConfigFileLocation(path, false);

        verify(configLoader).addConfigLocation(path);
        verify(configLoader, never()).save();
        verify(configFileMonitor).monitor();
    }

    @Test
    public void testFileDeleted() throws Exception {
        configFileMonitor.fileDeleted(fileChangeEvent);

        verify(platformSettingsService).evictMotechSettingsCache();

        assertNull(configFileMonitor.getCurrentSettings());
    }

    @Test
    public void testFileChanged() throws Exception {
        when(configLoader.loadConfig()).thenReturn(motechSettings);

        configFileMonitor.fileChanged(fileChangeEvent);

        verify(configLoader).loadConfig();
        verify(platformSettingsService).evictMotechSettingsCache();

        assertCurrentSettings();
    }

    private void assertCurrentSettings() {
        ConfigFileSettings currentSettings = configFileMonitor.getCurrentSettings();

        assertNotNull(currentSettings);
        assertEquals(motechSettings, currentSettings);
    }

}
