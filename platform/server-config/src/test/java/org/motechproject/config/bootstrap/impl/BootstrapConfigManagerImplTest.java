package org.motechproject.config.bootstrap.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.config.MotechConfigurationException;
import org.motechproject.config.bootstrap.ConfigFileReader;
import org.motechproject.config.bootstrap.Environment;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigLocation;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.filestore.ConfigLocationFileStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.config.bootstrap.impl.BootstrapConfigManagerImpl.BOOTSTRAP_PROPERTIES;
import static org.motechproject.config.domain.BootstrapConfig.DB_URL;
import static org.motechproject.config.domain.BootstrapConfig.TENANT_ID;
import static org.motechproject.config.domain.ConfigLocation.FileAccessType.READABLE;
import static org.motechproject.config.domain.ConfigLocation.FileAccessType.WRITABLE;


public class BootstrapConfigManagerImplTest {

    private final String bootstrapFileLocation = "file_location";
    private final String bootstrapFile = bootstrapFileLocation + "/bootstrap.properties";
    private final String dbUrl = "http://localhost:5984";
    private final String username = "user";
    private final String password = "pass";
    private final String tenantId = "test_tenant_id";
    private final String configSource = ConfigSource.FILE.getName();
    @Mock
    private Environment environmentMock;
    @Mock
    private ConfigFileReader configFileReaderMock;
    @Mock
    private ConfigLocationFileStore configLocationFileStoreMock;

    private BootstrapConfigManagerImpl bootstrapConfigManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bootstrapConfigManager = new BootstrapConfigManagerImpl(configFileReaderMock, environmentMock, configLocationFileStoreMock);
    }

    @Test
    public void shouldReturnBootstrapConfigFromFileSpecifiedInTheEnvironmentVariable() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(bootstrapFileLocation);

        Properties properties = new Properties();
        properties.put("db.url", dbUrl);
        properties.put("db.username", username);
        properties.put("db.password", password);
        properties.put("tenant.id", tenantId);
        properties.put("config.source", configSource);

        when(configFileReaderMock.getProperties(new File(bootstrapFile))).thenReturn(properties);

        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.FILE);

        assertThat(bootstrapConfigManager.loadBootstrapConfig(), IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfConfigFileReaderCanNotReadFileSpecifiedInEnvironmentVariable() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(bootstrapFileLocation);
        when(configFileReaderMock.getProperties(new File(bootstrapFile))).thenThrow(new IOException());

        bootstrapConfigManager.loadBootstrapConfig();
    }

    @Test
    public void shouldReturnBootStrapConfigValuesFromEnvironmentVariableWhenMotechConfigDirIsNotSpecified() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(dbUrl);
        when(environmentMock.getDBUsername()).thenReturn(username);
        when(environmentMock.getDBPassword()).thenReturn(password);
        when(environmentMock.getTenantId()).thenReturn(tenantId);
        when(environmentMock.getConfigSource()).thenReturn(configSource);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.FILE);

        assertThat(bootstrapConfigManager.loadBootstrapConfig(), IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfTenantIdIsNotSpecified() {
        when(environmentMock.getDBUrl()).thenReturn(dbUrl);
        when(environmentMock.getConfigSource()).thenReturn("FILE");
        when(environmentMock.getTenantId()).thenReturn(null);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.FILE);

        BootstrapConfig actualBootStrapConfig = bootstrapConfigManager.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfConfigSourceIsNotSpecified() {
        when(environmentMock.getDBUrl()).thenReturn(dbUrl);
        when(environmentMock.getConfigSource()).thenReturn(null);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.UI);

        BootstrapConfig actualBootStrapConfig = bootstrapConfigManager.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnBootStrapConfigFromFileAtDefaultLocation_If_NoEnvironmentVariablesAreSpecified() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(null);

        File bootstrapConfigFile = mockDefaultBootstrapFile();

        Properties properties = new Properties();
        properties.setProperty("db.url", dbUrl);
        when(configFileReaderMock.getProperties(bootstrapConfigFile)).thenReturn(properties);

        assertThat(bootstrapConfigManager.loadBootstrapConfig(), IsEqual.equalTo(new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.UI)));
    }

    private File mockDefaultBootstrapFile() throws IOException {
        File bootstrapConfigFile = new File(System.getProperty("user.home") + "/" + "/bootstrap.properties");
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocationMock = mock(ConfigLocation.class);
        configLocationList.add(configLocationMock);
        when(configLocationFileStoreMock.getAll()).thenReturn(configLocationList);
        when(configLocationMock.getFile(BOOTSTRAP_PROPERTIES, READABLE)).thenReturn(bootstrapConfigFile);

        return bootstrapConfigFile;
    }

    @Test
    public void shouldLoadPropertiesInTheCorrectOrder() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(null);
        when(configLocationFileStoreMock.getAll()).thenReturn(new ArrayList<ConfigLocation>());

        bootstrapConfigManager.loadBootstrapConfig();

        InOrder inOrder = inOrder(environmentMock, configLocationFileStoreMock, configFileReaderMock);

        inOrder.verify(environmentMock).getConfigDir();
        inOrder.verify(environmentMock).getDBUrl();
        inOrder.verify(configLocationFileStoreMock).getAll();
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfReadingTheBootstrapFileFails() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(null);
        mockDefaultBootstrapFile();
        doThrow(new IOException()).when(configFileReaderMock).getProperties(Matchers.any(File.class));

        bootstrapConfigManager.loadBootstrapConfig();
    }

    @Test
    public void shouldReturnNullIfNoneOfTheFilesInTheDefaultLocationIsReadable() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(null);
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocation1 = mock(ConfigLocation.class);
        configLocationList.add(configLocation1);
        when(configLocation1.getFile(BOOTSTRAP_PROPERTIES, READABLE)).thenThrow(new MotechConfigurationException("Failed"));
        ConfigLocation configLocation2 = mock(ConfigLocation.class);
        configLocationList.add(configLocation2);
        when(configLocation2.getFile(BOOTSTRAP_PROPERTIES, READABLE)).thenThrow(new MotechConfigurationException("Failed"));
        when(configLocationFileStoreMock.getAll()).thenReturn(configLocationList);

        assertNull(bootstrapConfigManager.loadBootstrapConfig());
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowMotechConfigurationExceptionIfSavingBootstrapPropertiesFailed() throws IOException {
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocationMock = mock(ConfigLocation.class);
        configLocationList.add(configLocationMock);
        when(configLocationFileStoreMock.getAll()).thenReturn(configLocationList);
        File fileMock = mock(File.class);
        when(configLocationMock.getFile(BOOTSTRAP_PROPERTIES, WRITABLE)).thenReturn(fileMock);
        File parentDirectory = mock(File.class);
        when(fileMock.getParentFile()).thenReturn(parentDirectory);
        doThrow(new IOException("IO Error")).when(fileMock).createNewFile();

        bootstrapConfigManager.saveBootstrapConfig(new BootstrapConfig(new DBConfig("http://testurl", "testuser", "testpass"), "test", ConfigSource.UI));
    }

    @Test
    public void shouldSaveBootstrapConfigToPropertiesFileInDefaultLocation() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://some_url", "some_username", "some_password"), "tenentId", ConfigSource.FILE);

        String tempDir = System.getProperty("java.io.tmpdir") + "/config";
        List<ConfigLocation> configLocationList = new ArrayList<>();
        configLocationList.add(new ConfigLocation(tempDir));
        when(configLocationFileStoreMock.getAll()).thenReturn(configLocationList);

        bootstrapConfigManager.saveBootstrapConfig(bootstrapConfig);

        Properties savedBootstrapProperties = new Properties();
        savedBootstrapProperties.load(new FileInputStream(new File(tempDir, "bootstrap.properties")));
        assertNotNull(savedBootstrapProperties);
        assertThat(savedBootstrapProperties.getProperty(DB_URL), IsEqual.equalTo("http://some_url"));
        assertThat(savedBootstrapProperties.getProperty(TENANT_ID), IsEqual.equalTo("tenentId"));
    }
}
