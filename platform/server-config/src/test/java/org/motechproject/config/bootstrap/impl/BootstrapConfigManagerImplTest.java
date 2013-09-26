package org.motechproject.config.bootstrap.impl;

import org.apache.commons.configuration.PropertiesConfiguration;
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
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.filestore.ConfigLocationFileStore;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.motechproject.config.bootstrap.impl.BootstrapConfigManagerImpl.DEFAULT_BOOTSTRAP_CONFIG_DIR_PROP;
import static org.motechproject.config.domain.BootstrapConfig.DB_URL;
import static org.motechproject.config.domain.BootstrapConfig.TENANT_ID;

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
    private ConfigLocationFileStore configLocationFileStore;

    private BootstrapConfigManagerImpl bootstrapConfigManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bootstrapConfigManager = new BootstrapConfigManagerImpl(configFileReaderMock, environmentMock, configLocationFileStore);
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

        Properties properties = new Properties();
        properties.setProperty("db.url", dbUrl);
        when(configFileReaderMock.getProperties(new File("path_to_default_properties/bootstrap.properties"))).thenReturn(properties);

        assertThat(bootstrapConfigManager.loadBootstrapConfig(), IsEqual.equalTo(new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.UI)));
    }

    @Test
    public void shouldLoadPropertiesInTheCorrectOrder() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(null);
        final Properties properties = new Properties();
        properties.setProperty("db.url", dbUrl);
        when(configFileReaderMock.getProperties(new File("path_to_default_properties/bootstrap.properties"))).thenReturn(properties);

        bootstrapConfigManager.loadBootstrapConfig();

        InOrder inOrder = inOrder(environmentMock, configFileReaderMock);

        inOrder.verify(environmentMock).getConfigDir();
        inOrder.verify(environmentMock).getDBUrl();
        inOrder.verify(configFileReaderMock).getProperties(new File("path_to_default_properties/bootstrap.properties"));
    }

//    @Test
//    public void shouldReturnTheDefaultPropertiesLocationIfNotSpecified() {
//        bootstrapConfigManager = new BootstrapConfigManagerImpl(configFileReaderMock, environmentMock, new PropertiesConfiguration());
//        assertThat(bootstrapConfigManager.getDefaultBootstrapConfigDir(), is(equalTo(bootstrapConfigManager.DEFAULT_BOOTSTRAP_CONFIG_DIR)));
//    }

    @Test
    public void shouldReturnNullIfTheBootstrapFileInDefaultLocationIsNotAccessible() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(null);
        doThrow(new IOException()).when(configFileReaderMock).getProperties(Matchers.any(File.class));

        assertNull(bootstrapConfigManager.loadBootstrapConfig());
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowMotechConfigurationExceptionIfSavingBootstrapPropertiesFailed() throws IOException {
        BootstrapConfigManagerImpl bootstrapConfigManagerStub = spy(bootstrapConfigManager);
        File fileMock = mock(File.class);
        when(bootstrapConfigManagerStub.getDefaultBootstrapFile()).thenReturn(fileMock);
        File parentFile = mock(File.class);
        when(fileMock.getParentFile()).thenReturn(parentFile);

        doThrow(new IOException()).when(fileMock).createNewFile();
        bootstrapConfigManagerStub.saveBootstrapConfig(new BootstrapConfig(new DBConfig("http://testurl", "testuser", "testpass"), "test", ConfigSource.UI));
    }

//    @Test
//    public void shouldSaveBootstrapConfigToPropertiesFileInDefaultLocation() throws IOException {
//        File tempFile = File.createTempFile("someprefix", "y");
//        tempFile.deleteOnExit();
//        String configDirectory = tempFile.getParent();
//        PropertiesConfiguration propertiesConfiguration = mock(PropertiesConfiguration.class);
//        when(propertiesConfiguration.getString(DEFAULT_BOOTSTRAP_CONFIG_DIR_PROP)).thenReturn(configDirectory);
//        bootstrapConfigManager = new BootstrapConfigManagerImpl(configFileReaderMock, environmentMock, propertiesConfiguration);
//        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://some_url", "some_username", "some_password"), "tenentId", ConfigSource.FILE);
//
//        bootstrapConfigManager.saveBootstrapConfig(bootstrapConfig);
//
//        Properties savedBootstrapProperties = new Properties();
//        savedBootstrapProperties.load(new FileInputStream(new File(configDirectory, "bootstrap.properties")));
//        assertNotNull(savedBootstrapProperties);
//        assertThat(savedBootstrapProperties.getProperty(DB_URL), is("http://some_url"));
//        assertThat(savedBootstrapProperties.getProperty(TENANT_ID),is("tenentId"));
//    }
}
