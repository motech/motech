package org.motechproject.config.service.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.domain.ModulePropertiesRecord;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.config.service.BundlePropertiesService;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.service.ConfigLoader;
import org.motechproject.server.config.service.SettingService;
import org.motechproject.testing.utils.FileHelper;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigurationServiceTest {

    @Mock
    private CoreConfigurationService coreConfigurationService;

    @Mock
    private BundlePropertiesService bundlePropertiesService;

    @Mock
    private SettingService settingService;

    @Mock
    private Properties defaultConfig;

    @Mock
    private ConfigLoader configLoader;

    @Mock
    private ResourceLoader resourceLoader;

    @Captor
    ArgumentCaptor<ModulePropertiesRecord> propertieCaptor;

    private ConfigurationService configurationService;

    @Before
    public void setUp() {
        initMocks(this);
        configurationService = new ConfigurationServiceImpl(coreConfigurationService, settingService,
                bundlePropertiesService, configLoader, resourceLoader);
        configurationService.evictMotechSettingsCache();

        if (configurationService instanceof ConfigurationServiceImpl) {
            ((ConfigurationServiceImpl) configurationService).setDefaultConfig(defaultConfig);
        }
    }

    @Test
    public void shouldLoadBootstrapDBConfiguration() {
        BootstrapConfig expectedConfig = new BootstrapConfig(new SQLDBConfig("jdbc:mysql://localhost:3306/", "com.mysql.jdbc.Driver", null, null), null, null, "tcp://localhost:61616");
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(expectedConfig);

        BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();
        assertNotNull(bootstrapConfig);

        assertThat(bootstrapConfig, IsEqual.equalTo(bootstrapConfig));
    }

    @Test
    public void shouldSaveBootstrapConfig() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new SQLDBConfig("jdbc:mysql://localhost:3306/", "com.mysql.jdbc.Driver", null, null), ConfigSource.FILE, null, "tcp://localhost:61616");

        configurationService.save(bootstrapConfig);

        verify(coreConfigurationService).saveBootstrapConfig(bootstrapConfig);
    }

    @Test
    public void shouldAddOrUpdateConfigWhileProcessingExistingConfigs() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        List<ModulePropertiesRecord> dbRecords = new ArrayList<>();

        File file1 = FileHelper.getResourceFile("config/org.motechproject.motech-module1/somemodule.properties");
        ModulePropertiesRecord dbRecord1 = ModulePropertiesRecord.buildFrom(file1);
        dbRecords.add(dbRecord1);

        when(bundlePropertiesService.retrieveAll()).thenReturn(dbRecords);

        configurationService.processExistingConfigs(Arrays.asList(file1));

        verify(bundlePropertiesService).create(propertieCaptor.capture());
        ModulePropertiesRecord actualRecord = propertieCaptor.getValue();
        assertEquals("somemodule.properties", actualRecord.getFilename());

        verify(settingService, never()).create((SettingsRecord) any());
        verify(bundlePropertiesService, never()).delete((ModulePropertiesRecord) any());
    }

    @Test
    public void shouldUpdatePlatformCoreConfigWhileProcessingExistingConfigs() {
        List<SettingsRecord> dbRecords = new ArrayList<>();
        dbRecords.add(new SettingsRecord());
        when(configLoader.loadMotechSettings()).thenReturn(new SettingsRecord());
        when(settingService.retrieve("id", 1)).thenReturn(null);

        File file = FileHelper.getResourceFile("config/motech-settings.properties");
        configurationService.processExistingConfigs(Arrays.asList(file));

        verify(settingService).create((SettingsRecord) any());

        verify(bundlePropertiesService, never()).create((ModulePropertiesRecord) any());
        verify(bundlePropertiesService, never()).update((ModulePropertiesRecord) any());
        verify(bundlePropertiesService, never()).delete((ModulePropertiesRecord) any());
    }

    @Test
    public void shouldDeleteProperties() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        List<ModulePropertiesRecord> dbRecords = new ArrayList<>();

        File file1 = FileHelper.getResourceFile("config/org.motechproject.motech-module1/somemodule.properties");
        ModulePropertiesRecord dbRecord1 = ModulePropertiesRecord.buildFrom(file1);
        dbRecords.add(dbRecord1);
        when(bundlePropertiesService.retrieveAll()).thenReturn(dbRecords);

        File file2 = FileHelper.getResourceFile("config/org.motechproject.motech-module2/raw/somemodule.json");
        ModulePropertiesRecord dbRecord2 = ModulePropertiesRecord.buildFrom(file2);
        dbRecords.add(dbRecord2);

        configurationService.processExistingConfigs(Arrays.asList(file1));

        verify(bundlePropertiesService).create(propertieCaptor.capture());
        ModulePropertiesRecord addedOrUpdatedRecord = propertieCaptor.getValue();
        assertEquals("somemodule.properties", addedOrUpdatedRecord.getFilename());

        verify(bundlePropertiesService).delete(propertieCaptor.capture());
        ModulePropertiesRecord deletedRecord = propertieCaptor.getValue();
        assertEquals("somemodule.json", deletedRecord.getFilename());
    }

    @Test
    public void shouldDeleteModulePropertiesRecordCorrespondingToAFile() {
        File fileToDelete = FileHelper.getResourceFile("config/org.motechproject.motech-module1/somemodule.properties");
        final String module = fileToDelete.getParentFile().getName();

        ModulePropertiesRecord record = new ModulePropertiesRecord();
        record.setFilename("somemodule.properties");
        when(bundlePropertiesService.findByBundle(module)).thenReturn(Arrays.asList(record));

        configurationService.deleteByBundle(module);

        ArgumentCaptor<ModulePropertiesRecord> recordCaptor = ArgumentCaptor.forClass(ModulePropertiesRecord.class);
        verify(bundlePropertiesService).delete(recordCaptor.capture());
        ModulePropertiesRecord deletedRecord = recordCaptor.getValue();
        assertEquals("somemodule.properties", deletedRecord.getFilename());
    }

    @Test
    public void shouldGetEmptyPropertiesWhenNoPropertiesAreFound() throws java.io.IOException {
        final String module = "mds";
        final String filename = "filename";
        when(bundlePropertiesService.findByBundleAndFileName(module, filename)).thenReturn(null);

        final Properties moduleProperties = configurationService.getBundleProperties(module, filename, null);
        assertNotNull(moduleProperties);
    }

    @Test
    public void shouldUpdateMotechSettings() {
        when(configLoader.loadMotechSettings()).thenReturn(new SettingsRecord());
        final SettingsRecord settingsRecord = new SettingsRecord();
        when(settingService.retrieve("id", 1)).thenReturn(settingsRecord);
        configurationService.addOrUpdate(FileHelper.getResourceFile("config/motech-settings.properties"));
        verify(settingService).update(settingsRecord);
    }

    @Test
    public void shouldCreateModuleProperties() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        configurationService.addOrUpdate(FileHelper.getResourceFile("config/org.motechproject.motech-module2/raw/somemodule.json"));
        verify(bundlePropertiesService).create((ModulePropertiesRecord) any());
    }

    @Test
    public void shouldUpdateModuleProperties() {
        ModulePropertiesRecord moduleRecord = new ModulePropertiesRecord();
        moduleRecord.setFilename("somemodule.json");
        moduleRecord.setBundle("org.motechproject.motech-module2");
        when(bundlePropertiesService.findByBundleAndFileName("org.motechproject.motech-module2", "somemodule.json")).
                thenReturn(Arrays.asList(moduleRecord));
        ClassLoader classLoader = this.getClass().getClassLoader();
        configurationService.addOrUpdate(FileHelper.getResourceFile("config/org.motechproject.motech-module2/raw/somemodule.json"));
        verify(bundlePropertiesService).update((ModulePropertiesRecord) any());
    }

    @Test
    public void shouldIndicateThatConfigFilesAreNotRequiredWhenConfigSourceIsUI() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new SQLDBConfig("jdbc:mysql://localhost:3306/", "com.mysql.jdbc.Driver", null, null), ConfigSource.UI, null, "tcp://localhost:61616");
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        assertFalse(configurationService.requiresConfigurationFiles());
    }


    @Test
    public void shouldIndicateThatConfigFilesAreNotRequiredWhenPlatformConfigurationFileIsPresent() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new SQLDBConfig("jdbc:mysql://localhost:3306/", "com.mysql.jdbc.Driver", null, null), ConfigSource.FILE, null, "tcp://localhost:61616");
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        ConfigLocation configLocation = mock(ConfigLocation.class);
        when(configLocation.hasPlatformConfigurationFile()).thenReturn(true);

        when(coreConfigurationService.getConfigLocation()).thenReturn(configLocation);

        assertFalse(configurationService.requiresConfigurationFiles());
    }

    @Test
    public void shouldIndicateThatConfigFilesAreRequiredWhenPlatformConfigurationFileIsMissing() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new SQLDBConfig("jdbc:mysql://localhost:3306/", "com.mysql.jdbc.Driver", null, null), ConfigSource.FILE, null, "tcp://localhost:61616");
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        ConfigLocation configLocation = mock(ConfigLocation.class);
        when(configLocation.hasPlatformConfigurationFile()).thenReturn(false);

        when(coreConfigurationService.getConfigLocation()).thenReturn(configLocation);

        assertTrue(configurationService.requiresConfigurationFiles());
    }
}
