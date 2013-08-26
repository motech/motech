package org.motechproject.config.bootstrap.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.config.MotechConfigurationException;
import org.motechproject.config.bootstrap.ConfigFileReader;
import org.motechproject.config.bootstrap.Environment;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class BootstrapConfigLoaderImplTest {

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
    private BootstrapConfigLoaderImpl bootstrapConfigLoader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Properties configProperties = new Properties();
        configProperties.setProperty("default.bootstrap.config.dir", "path_to_default_properties");
        bootstrapConfigLoader = new BootstrapConfigLoaderImpl(configFileReaderMock, environmentMock, configProperties);
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

        assertThat(bootstrapConfigLoader.loadBootstrapConfig(), IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfConfigFileReaderCanNotReadFileSpecifiedInEnvironmentVariable() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(bootstrapFileLocation);
        when(configFileReaderMock.getProperties(new File(bootstrapFile))).thenThrow(new IOException());

        bootstrapConfigLoader.loadBootstrapConfig();
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

        assertThat(bootstrapConfigLoader.loadBootstrapConfig(), IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfTenantIdIsNotSpecified() {
        when(environmentMock.getDBUrl()).thenReturn(dbUrl);
        when(environmentMock.getConfigSource()).thenReturn("FILE");
        when(environmentMock.getTenantId()).thenReturn(null);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.FILE);

        BootstrapConfig actualBootStrapConfig = bootstrapConfigLoader.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfConfigSourceIsNotSpecified() {
        when(environmentMock.getDBUrl()).thenReturn(dbUrl);
        when(environmentMock.getConfigSource()).thenReturn(null);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.UI);

        BootstrapConfig actualBootStrapConfig = bootstrapConfigLoader.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnBootStrapConfigFromFileAtDefaultLocation_If_NoEnvironmentVariablesAreSpecified() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(null);

        Properties properties = new Properties();
        properties.setProperty("db.url", dbUrl);
        when(configFileReaderMock.getProperties(new File("path_to_default_properties/bootstrap.properties"))).thenReturn(properties);

        assertThat(bootstrapConfigLoader.loadBootstrapConfig(), IsEqual.equalTo(new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.UI)));
    }

    @Test
    public void shouldLoadPropertiesInTheCorrectOrder() throws IOException {
        when(environmentMock.getConfigDir()).thenReturn(null);
        when(environmentMock.getDBUrl()).thenReturn(null);
        final Properties properties = new Properties();
        properties.setProperty("db.url", dbUrl);
        when(configFileReaderMock.getProperties(new File("path_to_default_properties/bootstrap.properties"))).thenReturn(properties);

        bootstrapConfigLoader.loadBootstrapConfig();

        InOrder inOrder = inOrder(environmentMock, configFileReaderMock);

        inOrder.verify(environmentMock).getConfigDir();
        inOrder.verify(environmentMock).getDBUrl();
        inOrder.verify(configFileReaderMock).getProperties(new File("path_to_default_properties/bootstrap.properties"));
    }


    @Test
    public void shouldReturnTheDefaultPropertiesLocationIfNotSpecified(){
        bootstrapConfigLoader = new BootstrapConfigLoaderImpl(configFileReaderMock, environmentMock, new Properties());
        assertThat(bootstrapConfigLoader.getDefaultBootstrapConfigDir(), is(equalTo(bootstrapConfigLoader.DEFAULT_BOOTSTRAP_CONFIG_DIR)));
    }
}
