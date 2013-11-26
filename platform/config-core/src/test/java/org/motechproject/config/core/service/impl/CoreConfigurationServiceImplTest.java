package org.motechproject.config.core.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.bootstrap.Environment;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.filestore.ConfigFileReader;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.config.core.domain.BootstrapConfig.DB_URL;
import static org.motechproject.config.core.domain.BootstrapConfig.TENANT_ID;
import static org.motechproject.config.core.domain.ConfigLocation.FileAccessType.READABLE;
import static org.motechproject.config.core.domain.ConfigLocation.FileAccessType.WRITABLE;
import static org.motechproject.config.core.service.impl.CoreConfigurationServiceImpl.BOOTSTRAP_PROPERTIES;


public class CoreConfigurationServiceImplTest {

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

    private CoreConfigurationServiceImpl coreConfigurationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        coreConfigurationService = new CoreConfigurationServiceImpl(configFileReaderMock, environmentMock, configLocationFileStoreMock);
    }

    @Test
    public void shouldReturnBootstrapConfigFromFileSpecifiedInTheEnvironmentVariable() throws IOException {
        Mockito.when(environmentMock.getConfigDir()).thenReturn(bootstrapFileLocation);

        Properties properties = new Properties();
        properties.put("db.url", dbUrl);
        properties.put("db.username", username);
        properties.put("db.password", password);
        properties.put("tenant.id", tenantId);
        properties.put("config.source", configSource);

        Mockito.when(configFileReaderMock.getProperties(new File(bootstrapFile))).thenReturn(properties);

        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.FILE);

        assertThat(coreConfigurationService.loadBootstrapConfig(), equalTo(expectedBootstrapConfig));
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfConfigFileReaderCanNotReadFileSpecifiedInEnvironmentVariable() throws IOException {
        Mockito.when(environmentMock.getConfigDir()).thenReturn(bootstrapFileLocation);
        Mockito.when(configFileReaderMock.getProperties(new File(bootstrapFile))).thenThrow(new IOException());

        coreConfigurationService.loadBootstrapConfig();
    }

    @Test
    public void shouldReturnBootStrapConfigValuesFromEnvironmentVariableWhenMotechConfigDirIsNotSpecified() throws IOException {
        Mockito.when(environmentMock.getConfigDir()).thenReturn(null);
        Mockito.when(environmentMock.getDBUrl()).thenReturn(dbUrl);
        Mockito.when(environmentMock.getDBUsername()).thenReturn(username);
        Mockito.when(environmentMock.getDBPassword()).thenReturn(password);
        Mockito.when(environmentMock.getTenantId()).thenReturn(tenantId);
        Mockito.when(environmentMock.getConfigSource()).thenReturn(configSource);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.FILE);

        assertThat(coreConfigurationService.loadBootstrapConfig(), equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfTenantIdIsNotSpecified() {
        Mockito.when(environmentMock.getDBUrl()).thenReturn(dbUrl);
        Mockito.when(environmentMock.getConfigSource()).thenReturn("FILE");
        Mockito.when(environmentMock.getTenantId()).thenReturn(null);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.FILE);

        BootstrapConfig actualBootStrapConfig = coreConfigurationService.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfConfigSourceIsNotSpecified() {
        Mockito.when(environmentMock.getDBUrl()).thenReturn(dbUrl);
        Mockito.when(environmentMock.getConfigSource()).thenReturn(null);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.UI);

        BootstrapConfig actualBootStrapConfig = coreConfigurationService.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnBootStrapConfigFromFileAtDefaultLocation_If_NoEnvironmentVariablesAreSpecified() throws IOException {
        Mockito.when(environmentMock.getConfigDir()).thenReturn(null);
        Mockito.when(environmentMock.getDBUrl()).thenReturn(null);

        File bootstrapConfigFile = mockDefaultBootstrapFile();

        Properties properties = new Properties();
        properties.setProperty("db.url", dbUrl);
        Mockito.when(configFileReaderMock.getProperties(bootstrapConfigFile)).thenReturn(properties);

        assertThat(coreConfigurationService.loadBootstrapConfig(), equalTo(new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.UI)));
    }

    private File mockDefaultBootstrapFile() throws IOException {
        File bootstrapConfigFile = new File(System.getProperty("user.home") + "/" + "/bootstrap.properties");
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocationMock = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocationMock);
        Mockito.when(configLocationFileStoreMock.getAll()).thenReturn(configLocationList);
        Mockito.when(configLocationMock.getFile(BOOTSTRAP_PROPERTIES, READABLE)).thenReturn(bootstrapConfigFile);

        return bootstrapConfigFile;
    }

    @Test
    public void shouldLoadPropertiesInTheCorrectOrder() throws IOException {
        Mockito.when(environmentMock.getConfigDir()).thenReturn(null);
        Mockito.when(environmentMock.getDBUrl()).thenReturn(null);
        Mockito.when(configLocationFileStoreMock.getAll()).thenReturn(new ArrayList<ConfigLocation>());

        coreConfigurationService.loadBootstrapConfig();

        InOrder inOrder = Mockito.inOrder(environmentMock, configLocationFileStoreMock, configFileReaderMock);

        inOrder.verify(environmentMock).getConfigDir();
        inOrder.verify(environmentMock).getDBUrl();
        inOrder.verify(configLocationFileStoreMock).getAll();
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfReadingTheBootstrapFileFails() throws IOException {
        Mockito.when(environmentMock.getConfigDir()).thenReturn(null);
        Mockito.when(environmentMock.getDBUrl()).thenReturn(null);
        mockDefaultBootstrapFile();
        Mockito.doThrow(new IOException()).when(configFileReaderMock).getProperties(Matchers.any(File.class));

        coreConfigurationService.loadBootstrapConfig();
    }

    @Test
    public void shouldReturnNullIfNoneOfTheFilesInTheDefaultLocationIsReadable() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(null);
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocation1 = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocation1);
        when(configLocation1.getFile(BOOTSTRAP_PROPERTIES, READABLE)).thenThrow(new MotechConfigurationException("Failed"));
        ConfigLocation configLocation2 = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocation2);
        when(configLocation2.getFile(BOOTSTRAP_PROPERTIES, READABLE)).thenThrow(new MotechConfigurationException("Failed"));
        when(configLocationFileStoreMock.getAll()).thenReturn(configLocationList);

        Assert.assertNull(coreConfigurationService.loadBootstrapConfig());
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowMotechConfigurationExceptionIfSavingBootstrapPropertiesFailed() throws IOException {
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocationMock = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocationMock);
        when(configLocationFileStoreMock.getAll()).thenReturn(configLocationList);
        File fileMock = Mockito.mock(File.class);
        when(configLocationMock.getFile(BOOTSTRAP_PROPERTIES, WRITABLE)).thenReturn(fileMock);
        File parentDirectory = Mockito.mock(File.class);
        when(fileMock.getParentFile()).thenReturn(parentDirectory);
        Mockito.doThrow(new IOException("IO Error")).when(fileMock).createNewFile();

        coreConfigurationService.saveBootstrapConfig(new BootstrapConfig(new DBConfig("http://testurl", "testuser", "testpass"), "test", ConfigSource.UI));
    }

    @Test
    public void shouldSaveBootstrapConfigToPropertiesFileInDefaultLocation() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://some_url", "some_username", "some_password"), "tenentId", ConfigSource.FILE);

        String tempDir = System.getProperty("java.io.tmpdir") + "/config";
        List<ConfigLocation> configLocationList = new ArrayList<>();
        configLocationList.add(new ConfigLocation(tempDir));
        Mockito.when(configLocationFileStoreMock.getAll()).thenReturn(configLocationList);

        coreConfigurationService.saveBootstrapConfig(bootstrapConfig);

        Properties savedBootstrapProperties = new Properties();
        savedBootstrapProperties.load(new FileInputStream(new File(tempDir, "bootstrap.properties")));
        assertNotNull(savedBootstrapProperties);
        assertThat(savedBootstrapProperties.getProperty(DB_URL), equalTo("http://some_url"));
        assertThat(savedBootstrapProperties.getProperty(TENANT_ID), equalTo("tenentId"));
    }

    @Test
    public void shouldGetConfigLocations() {
        final ArrayList<ConfigLocation> configLocations = new ArrayList<>();
        final ConfigLocation configLocation = new ConfigLocation("/etc");
        configLocations.add(configLocation);
        when(configLocationFileStoreMock.getAll()).thenReturn(configLocations);
        final ConfigLocation actualConfigLocation = coreConfigurationService.getConfigLocations().iterator().next();
        assertThat(actualConfigLocation, equalTo(configLocation));
    }

    @Test
    public void shouldAddConfigLocation() throws FileSystemException {
        final String location = "/etc";
        coreConfigurationService.addConfigLocation(location);
        verify(configLocationFileStoreMock).add(location);
    }
}
