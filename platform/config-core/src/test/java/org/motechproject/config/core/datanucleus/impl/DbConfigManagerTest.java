package org.motechproject.config.core.datanucleus.impl;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.datanucleus.DbConfigManager;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.environment.Environment;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.motechproject.config.core.filestore.ConfigPropertiesUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class DbConfigManagerTest {

    @InjectMocks
    private DbConfigManager dbConfigManager = new DbConfigManagerImpl();

    @Mock
    private Environment environment;

    @Mock
    private ConfigLocationFileStore configLocationFileStore;

    private File tempDir;

    @After
    public void tearDown() {
        if (tempDir != null) {
            FileUtils.deleteQuietly(tempDir);
        }
    }

    @Test
    public void shouldCopyFileWhenLoadingDnDataPropertiesFromClassPath() throws IOException {
        shouldCopyFileWhenLoadingPropertiesFromClassPath(ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME);
    }

    @Test
    public void shouldCopyFileWhenLoadingDnSchemaPropertiesFromClassPath() throws IOException {
        shouldCopyFileWhenLoadingPropertiesFromClassPath(ConfigurationConstants.DATANUCLEUS_SCHEMA_SETTINGS_FILE_NAME);
    }

    @Test
    public void shouldCopyFileWhenLoadingFlywayDataPropertiesFromClassPath() throws IOException {
        shouldCopyFileWhenLoadingPropertiesFromClassPath(ConfigurationConstants.FLYWAY_DATA_SETTINGS_FILE_NAME);
    }

    @Test
    public void shouldCopyFileWhenLoadingFlywaySchemaPropertiesFromClassPath() throws IOException {
        shouldCopyFileWhenLoadingPropertiesFromClassPath(ConfigurationConstants.FLYWAY_SCHEMA_SETTINGS_FILE_NAME);
    }

    private void shouldCopyFileWhenLoadingPropertiesFromClassPath(String filename) throws IOException {
        when(environment.getDatanucleusDataProperties()).thenReturn(new Properties());
        when(environment.getConfigDir()).thenReturn("");

        tempDir = Files.createTempDir();
        File file = new File(tempDir, filename);

        List<ConfigLocation> configLocationList = new ArrayList<>();

        configLocationList.add(new ConfigLocation(tempDir.getAbsolutePath()));
        when(configLocationFileStore.getAllConfigLocations()).thenReturn(configLocationList);

        useDbManagerToGetCorrectFile(filename);

        Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream(file)) {
            properties.load(is);
        }

        assertEquals(getCompleteProperties(filename), properties);
    }

    @Test
    public void shouldGetDnDataPropertiesFromEnvironment() throws IOException {
        shouldGetPropertiesFromEnvironment(ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME);
    }

    @Test
    public void shouldGetDnSchemaPropertiesFromEnvironment() throws IOException {
        shouldGetPropertiesFromEnvironment(ConfigurationConstants.DATANUCLEUS_SCHEMA_SETTINGS_FILE_NAME);
    }

    @Test
    public void shouldGetFlywayDataPropertiesFromEnvironment() throws IOException {
        shouldGetPropertiesFromEnvironment(ConfigurationConstants.FLYWAY_DATA_SETTINGS_FILE_NAME);
    }

    @Test
    public void shouldGetFlywaySchemaPropertiesFromEnvironment() throws IOException {
        shouldGetPropertiesFromEnvironment(ConfigurationConstants.FLYWAY_SCHEMA_SETTINGS_FILE_NAME);
    }

    private void shouldGetPropertiesFromEnvironment(String fileName) throws IOException {
        String varName = getEnvVarForFile(fileName);

        when(environment.getProperties(varName))
                .thenReturn(getCompleteProperties(fileName));
        when(environment.getConfigDir()).thenReturn("");

        List<ConfigLocation> configLocationList = new ArrayList<>();
        when(configLocationFileStore.getAllConfigLocations()).thenReturn(configLocationList);

        useDbManagerToGetCorrectFile(fileName);

        verify(configLocationFileStore, times(0)).getAllConfigLocations();
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test
    public void shouldReturnConfigFromFileSpecifiedInTheEnvironmentVariable() throws IOException {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        when(environment.getConfigDir()).thenReturn("file_location");

        Properties properties = getCompleteProperties(ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME);
        Mockito.when(ConfigPropertiesUtils.getPropertiesFromFile(new File("file_location",
                ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME))).thenReturn(properties);

        assertEquals(getCompleteProperties(ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME), dbConfigManager.getDatanucleusDataProperties());
    }

    @Test
    public void shouldLoadDnDataPropertiesFromDefaultConfigLocation() throws IOException {
        shouldLoadPropertiesFromDefaultConfigLocation(ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME);
    }

    @Test
    public void shouldLoadDnSchemaPropertiesFromDefaultConfigLocation() throws IOException {
        shouldLoadPropertiesFromDefaultConfigLocation(ConfigurationConstants.DATANUCLEUS_SCHEMA_SETTINGS_FILE_NAME);
    }

    @Test
    public void shouldLoadFlywayDataPropertiesFromDefaultConfigLocation() throws IOException {
        shouldLoadPropertiesFromDefaultConfigLocation(ConfigurationConstants.FLYWAY_DATA_SETTINGS_FILE_NAME);
    }

    @Test
    public void shouldLoadFlywaySchemaPropertiesFromDefaultConfigLocation() throws IOException {
        shouldLoadPropertiesFromDefaultConfigLocation(ConfigurationConstants.FLYWAY_SCHEMA_SETTINGS_FILE_NAME);
    }

    private void shouldLoadPropertiesFromDefaultConfigLocation(String filename) throws IOException {
        when(environment.getDatanucleusDataProperties()).thenReturn(new Properties());
        when(environment.getConfigDir()).thenReturn("");

        tempDir = Files.createTempDir();
        File file = new File(tempDir, filename);

        List<ConfigLocation> configLocationList = new ArrayList<>();

        Properties properties = getCompleteProperties(filename);
        try (FileOutputStream os = new FileOutputStream(file)) {
            properties.store(os, null);
        }

        configLocationList.add(new ConfigLocation(tempDir.getAbsolutePath()));
        when(configLocationFileStore.getAllConfigLocations()).thenReturn(configLocationList);

        properties = useDbManagerToGetCorrectFile(filename);

        assertEquals(getCompleteProperties(filename), properties);
    }

    private Properties getCompleteProperties(String filename) throws IOException {
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource(filename);
        try (InputStream is = resource.getInputStream()) {
            properties.load(is);
        }
        return properties;
    }

    private Properties useDbManagerToGetCorrectFile(String filename) {
        switch (filename) {
            case ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME:
                return dbConfigManager.getDatanucleusDataProperties();
            case ConfigurationConstants.DATANUCLEUS_SCHEMA_SETTINGS_FILE_NAME:
                return dbConfigManager.getDatanucleusSchemaProperties();
            case ConfigurationConstants.FLYWAY_DATA_SETTINGS_FILE_NAME:
                return dbConfigManager.getFlywayDataProperties();
            case ConfigurationConstants.FLYWAY_SCHEMA_SETTINGS_FILE_NAME:
                return dbConfigManager.getFlywaySchemaProperties();
            default:
                fail("Unsupported file " + filename);
                return null; // unreachable
        }
    }

    private String getEnvVarForFile(String filename) {
        switch (filename) {
            case ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME:
                return Environment.MOTECH_DATANUCLEUS_DATA_ROPERTIES;
            case ConfigurationConstants.DATANUCLEUS_SCHEMA_SETTINGS_FILE_NAME:
                return Environment.MOTECH_DATANUCLEUS_SCHEMA_PROPERTIES;
            case ConfigurationConstants.FLYWAY_DATA_SETTINGS_FILE_NAME:
                return Environment.MOTECH_FLYWAY_DATA_PROPERTIES;
            case ConfigurationConstants.FLYWAY_SCHEMA_SETTINGS_FILE_NAME:
                return Environment.MOTECH_FLYWAY_SCHEMA_PROPERTIES;
            default:
                fail("Unsupported file " + filename);
                return null; // unreachable
        }
    }
}
