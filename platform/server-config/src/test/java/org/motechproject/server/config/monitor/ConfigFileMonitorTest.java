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
import org.motechproject.event.listener.ServerEventRelay;
import org.motechproject.server.config.ConfigLoader;
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
    private static final String MOTECH_SETTINGS_FILE_NAME = "motech-settings.conf";
    private static final String ACTIVEMQ_FILE_NAME = "activemq.properties";
    private static final String SETTINGS_FILE_NAME = "settings.properties";

    @Mock
    ServerEventRelay serverEventRelay;

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
    ConfigFileMonitor configFileMonitor = new ConfigFileMonitor();

    private FileObject motechSettingsResource;
    private FileObject activemqResource;
    private FileObject settingsResource;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        configFileMonitor.setEventRelay(serverEventRelay);
        configFileMonitor.setConfigLoader(configLoader);
        configFileMonitor.setPlatformSettingsService(platformSettingsService);
        configFileMonitor.setSystemManager(systemManager);

        configFileMonitor.afterPropertiesSet();

        motechSettingsResource = VFS.getManager().resolveFile(String.format("res:%s", MOTECH_SETTINGS_FILE_NAME));
        activemqResource = VFS.getManager().resolveFile(String.format("res:%s", ACTIVEMQ_FILE_NAME));
        settingsResource = VFS.getManager().resolveFile(String.format("res:%s", SETTINGS_FILE_NAME));
    }

    @Test
    public void testChangeConfigFileLocation() throws Exception {
        when(resourceLoader.getResource(SETTINGS_FILE_NAME)).thenReturn(new ClassPathResource(SETTINGS_FILE_NAME));

        configFileMonitor.changeConfigFileLocation(SETTINGS_FILE_NAME, false);

        verify(configLoader).addConfigLocation(SETTINGS_FILE_NAME);
        verify(configLoader, never()).save();
        verify(configFileMonitor).monitor();
    }

    @Test
    public void testMotechSettingsFileDeleted() throws Exception {
        when(fileChangeEvent.getFile()).thenReturn(motechSettingsResource);

        configFileMonitor.fileDeleted(fileChangeEvent);

        verify(platformSettingsService).evictMotechSettingsCache();

        assertNull(configFileMonitor.getCurrentSettings());
    }

    @Test
    public void testActiveMqSettingsFileDeleted() throws Exception {
        when(fileChangeEvent.getFile()).thenReturn(activemqResource);

        configFileMonitor.fileDeleted(fileChangeEvent);

        verify(platformSettingsService).evictActiveMqSettingsCache();

        assertNull(configFileMonitor.getCurrentSettings());
    }

    @Test
    public void testBundleSettingsFileDeleted() throws Exception {
        when(fileChangeEvent.getFile()).thenReturn(settingsResource);

        configFileMonitor.fileDeleted(fileChangeEvent);

        verify(platformSettingsService).evictBundleSettingsCache();

        assertNull(configFileMonitor.getCurrentSettings());
    }

    @Test
    public void testMotechFileChanged() throws Exception {
        when(fileChangeEvent.getFile()).thenReturn(motechSettingsResource);
        when(configLoader.loadConfig()).thenReturn(motechSettings);

        configFileMonitor.fileChanged(fileChangeEvent);

        verify(configLoader).loadConfig();
        verify(platformSettingsService).evictMotechSettingsCache();

        assertCurrentSettings();
    }

    @Test
    public void testActiveMqFileChanged() throws Exception {
        when(fileChangeEvent.getFile()).thenReturn(activemqResource);
        when(configLoader.loadConfig()).thenReturn(motechSettings);

        configFileMonitor.fileChanged(fileChangeEvent);

        verify(configLoader).loadConfig();
        verify(platformSettingsService).evictActiveMqSettingsCache();

        assertCurrentSettings();
    }

    @Test
    public void testBundleFileChanged() throws Exception {
        when(fileChangeEvent.getFile()).thenReturn(settingsResource);
        when(configLoader.loadConfig()).thenReturn(motechSettings);

        configFileMonitor.fileChanged(fileChangeEvent);

        verify(configLoader).loadConfig();
        verify(platformSettingsService).evictBundleSettingsCache();
        assertCurrentSettings();
    }

    private void assertCurrentSettings() {
        ConfigFileSettings currentSettings = configFileMonitor.getCurrentSettings();

        assertNotNull(currentSettings);
        assertEquals(motechSettings, currentSettings);
    }

}
