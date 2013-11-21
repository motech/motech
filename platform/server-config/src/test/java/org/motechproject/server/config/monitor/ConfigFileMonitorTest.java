package org.motechproject.server.config.monitor;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.core.io.ResourceLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigFileMonitorTest {
    private static final String MOTECH_SETTINGS_FILE_NAME = "motech-settings.conf";
    private static final String SETTINGS_FILE_NAME = "settings.properties";

    @Mock
    PlatformSettingsService platformSettingsService;

    @Mock
    ConfigurationService configurationService;

    @Mock
    ResourceLoader resourceLoader;

    @Mock
    FileChangeEvent fileChangeEvent;

    @Mock
    SettingsRecord motechSettings;

    @Mock
    StandardFileSystemManager systemManager;

    @InjectMocks
    @Spy
    ConfigFileMonitor configFileMonitor = new ConfigFileMonitor();

    private FileObject motechSettingsResource;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        configFileMonitor.setPlatformSettingsService(platformSettingsService);
        configFileMonitor.setConfigurationService(configurationService);
        configFileMonitor.setSystemManager(systemManager);

        configFileMonitor.afterPropertiesSet();

        motechSettingsResource = VFS.getManager().resolveFile(String.format("res:%s", MOTECH_SETTINGS_FILE_NAME));
    }

    @Test
    public void testMotechSettingsFileDeleted() throws Exception {
        when(fileChangeEvent.getFile()).thenReturn(motechSettingsResource);

        configFileMonitor.fileDeleted(fileChangeEvent);

        verify(configurationService).evictMotechSettingsCache();

        assertNull(configFileMonitor.getCurrentSettings());
    }

    @Test
    public void testMotechFileChanged() throws Exception {
        when(fileChangeEvent.getFile()).thenReturn(motechSettingsResource);
        when(configurationService.loadConfig()).thenReturn(motechSettings);

        configFileMonitor.fileChanged(fileChangeEvent);

        verify(configurationService).loadConfig();
        verify(configurationService).evictMotechSettingsCache();

        assertCurrentSettings();
    }

    private void assertCurrentSettings() {
        MotechSettings currentSettings = configFileMonitor.getCurrentSettings();

        assertNotNull(currentSettings);
        assertEquals(motechSettings, currentSettings);
    }
}
