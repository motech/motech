package org.motechproject.config.service.impl;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigurationServiceTest {
    @Mock
    private CoreConfigurationService coreConfigurationService;

    @Mock
    private AllModuleProperties allModuleProperties;

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
    public void shouldBulkAddOrUpdateProperties() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        List<ModulePropertiesRecord> dbRecords = new ArrayList<>();

        File file1 = new File(classLoader.getResource("config/org.motechproject.motech-module1/somemodule.properties").getPath());
        ModulePropertiesRecord dbRecord1 = ModulePropertiesRecord.buildFrom(file1);
        dbRecord1.setId("1");
        dbRecords.add(dbRecord1);

        File file2 = new File(classLoader.getResource("config/org.motechproject.motech-module2/somemodule.json").getPath());
        ModulePropertiesRecord dbRecord2 = ModulePropertiesRecord.buildFrom(file2);
        dbRecord2.setId("2");
        dbRecords.add(dbRecord2);

        when(allModuleProperties.getAll()).thenReturn(dbRecords);

        configurationService.processExistingConfigs(Arrays.asList(file1, file2));

        verify(allModuleProperties).bulkAddOrUpdate(propertiesCaptor.capture());
        List<ModulePropertiesRecord> actualRecords = propertiesCaptor.getValue();
        assertEquals(2, actualRecords.size());
        assertEquals("somemodule.properties", actualRecords.get(0).getFilename());
        assertEquals("somemodule.json", actualRecords.get(1).getFilename());

        verify(allModuleProperties, never()).bulkDelete((List<ModulePropertiesRecord>) any());
    }

    @Test
    public void shouldBulkDeleteProperties() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        List<ModulePropertiesRecord> dbRecords = new ArrayList<>();

        File file1 = new File(classLoader.getResource("config/org.motechproject.motech-module1/somemodule.properties").getPath());
        ModulePropertiesRecord dbRecord1 = ModulePropertiesRecord.buildFrom(file1);
        dbRecord1.setId("1");
        dbRecords.add(dbRecord1);
        when(allModuleProperties.getAll()).thenReturn(dbRecords);

        File file2 = new File(classLoader.getResource("config/org.motechproject.motech-module2/somemodule.json").getPath());
        ModulePropertiesRecord dbRecord2 = ModulePropertiesRecord.buildFrom(file2);
        dbRecord2.setId("2");
        dbRecords.add(dbRecord2);

        configurationService.processExistingConfigs(Arrays.asList(file1));

        verify(allModuleProperties).bulkAddOrUpdate(propertiesCaptor.capture());
        List<ModulePropertiesRecord> addedOrUpdatedRecords = propertiesCaptor.getValue();
        assertEquals(1, addedOrUpdatedRecords.size());
        assertEquals("somemodule.properties", addedOrUpdatedRecords.get(0).getFilename());

        verify(allModuleProperties).bulkDelete(propertiesCaptor.capture());
        List<ModulePropertiesRecord> deletedRecords = propertiesCaptor.getValue();
        assertEquals(1, deletedRecords.size());
        assertEquals("somemodule.json", deletedRecords.get(0).getFilename());
    }

    @Test
    public void shouldDeleteModulePropertiesRecordCorrespondingToAFile() {
        File fileToDelete = new File(this.getClass().getClassLoader().getResource("config/org.motechproject.motech-module1/somemodule.properties").getPath());

        configurationService.delete(fileToDelete);

        ArgumentCaptor<ModulePropertiesRecord> recordCaptor = ArgumentCaptor.forClass(ModulePropertiesRecord.class);
        verify(allModuleProperties).remove(recordCaptor.capture());
        ModulePropertiesRecord deletedRecord = recordCaptor.getValue();
        assertEquals("somemodule.properties", deletedRecord.getFilename());
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
