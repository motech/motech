package org.motechproject.config.core.bootstrap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.bootstrap.impl.BootstrapManagerImpl;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.motechproject.config.core.filestore.PropertiesReader;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_URL;
import static org.motechproject.config.core.domain.BootstrapConfig.TENANT_ID;
import static org.motechproject.config.core.domain.ConfigLocation.FileAccessType.READABLE;
import static org.motechproject.config.core.domain.ConfigLocation.FileAccessType.WRITABLE;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PropertiesReader.class)
public class BootstrapManagerTest {

    private final String bootstrapFileLocation = "file_location";
    private final String bootstrapFile = bootstrapFileLocation + "/bootstrap.properties";
    private final String sqlUrl = "jdbc:mysql://localhost:3306/";
    private final String sqlUsername = "root";
    private final String sqlPassword = "password";
    private final String tenantId = "test_tenant_id";
    private static final String sqlDriver = "com.mysql.jdbc.Driver";
    private final String configSource = ConfigSource.FILE.getName();

    private BootstrapManager bootstrapManager;

    @Mock
    private Environment environment;

    @Mock
    private ConfigLocationFileStore configLocationFileStore;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(PropertiesReader.class);
        bootstrapManager = new BootstrapManagerImpl(configLocationFileStore, environment);
    }

    @Test
    public void shouldReturnBootstrapConfigFromFileSpecifiedInTheEnvironmentVariable() throws IOException {
        Mockito.when(environment.getConfigDir()).thenReturn(bootstrapFileLocation);

        Properties properties = new Properties();
        properties.put("sql.url", sqlUrl);
        properties.put("sql.user", sqlUsername);
        properties.put("sql.password", sqlPassword);
        properties.put("tenant.id", tenantId);
        properties.put("config.source", configSource);
        properties.put("sql.driver", sqlDriver);

        Mockito.when(PropertiesReader.getProperties(new File(bootstrapFile))).thenReturn(properties);

        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), tenantId, ConfigSource.FILE);

        assertThat(bootstrapManager.loadBootstrapConfig(), equalTo(expectedBootstrapConfig));
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfConfigFileReaderCanNotReadFileSpecifiedInEnvironmentVariable() throws IOException {
        Mockito.when(environment.getConfigDir()).thenReturn(bootstrapFileLocation);
        Mockito.when(PropertiesReader.getProperties(new File(bootstrapFile))).thenThrow(new IOException());

        bootstrapManager.loadBootstrapConfig();
    }

    @Test
    public void shouldReturnBootStrapConfigValuesFromEnvironmentVariableWhenMotechConfigDirIsNotSpecified() throws IOException {
        Mockito.when(environment.getConfigDir()).thenReturn(null);
        Mockito.when(environment.getSqlUrl()).thenReturn(sqlUrl);
        Mockito.when(environment.getSqlUsername()).thenReturn(sqlUsername);
        Mockito.when(environment.getSqlPassword()).thenReturn(sqlPassword);
        Mockito.when(environment.getTenantId()).thenReturn(tenantId);
        Mockito.when(environment.getConfigSource()).thenReturn(configSource);
        Mockito.when(environment.getSqlDriver()).thenReturn(sqlDriver);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), tenantId, ConfigSource.FILE);

        assertThat(bootstrapManager.loadBootstrapConfig(), equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfTenantIdIsNotSpecified() {
        Mockito.when(environment.getSqlUrl()).thenReturn(sqlUrl);
        Mockito.when(environment.getConfigSource()).thenReturn("FILE");
        Mockito.when(environment.getTenantId()).thenReturn(null);
        Mockito.when(environment.getSqlDriver()).thenReturn(sqlDriver);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), "DEFAULT", ConfigSource.FILE);

        BootstrapConfig actualBootStrapConfig = bootstrapManager.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfConfigSourceIsNotSpecified() {
        Mockito.when(environment.getSqlUrl()).thenReturn(sqlUrl);
        Mockito.when(environment.getConfigSource()).thenReturn(null);
        Mockito.when(environment.getSqlDriver()).thenReturn(sqlDriver);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), "DEFAULT", ConfigSource.UI);

        BootstrapConfig actualBootStrapConfig = bootstrapManager.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnBootStrapConfigFromFileAtDefaultLocation_If_NoEnvironmentVariablesAreSpecified() throws IOException {
        Mockito.when(environment.getConfigDir()).thenReturn(null);
        Mockito.when(environment.getSqlUrl()).thenReturn(null);
        Mockito.when(environment.getSqlDriver()).thenReturn(null);
        Mockito.when(environment.getSqlDriver()).thenReturn(null);

        File bootstrapConfigFile = mockDefaultBootstrapFile();

        Properties properties = new Properties();
        properties.setProperty("sql.url", sqlUrl);
        properties.setProperty("sql.driver", sqlDriver);
        Mockito.when(PropertiesReader.getProperties(bootstrapConfigFile)).thenReturn(properties);

        assertThat(bootstrapManager.loadBootstrapConfig(), equalTo(new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), "DEFAULT", ConfigSource.UI)));
    }

    private File mockDefaultBootstrapFile() throws IOException {
        File bootstrapConfigFile = new File(System.getProperty("user.home") + "/" + "/bootstrap.properties");
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocationMock = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocationMock);
        Mockito.when(configLocationFileStore.getAll()).thenReturn(configLocationList);
        Mockito.when(configLocationMock.getFile(BootstrapManager.BOOTSTRAP_PROPERTIES, READABLE)).thenReturn(bootstrapConfigFile);

        return bootstrapConfigFile;
    }

    @Test
    public void shouldLoadPropertiesInTheCorrectOrder() throws IOException {
        Mockito.when(environment.getConfigDir()).thenReturn(null);
        Mockito.when(environment.getSqlUrl()).thenReturn(null);
        Mockito.when(configLocationFileStore.getAll()).thenReturn(new ArrayList<ConfigLocation>());

        try {
            bootstrapManager.loadBootstrapConfig();
        } catch (MotechConfigurationException e) {
            // Ignore error because invocation order is to be verified.
        }

        InOrder inOrder = Mockito.inOrder(environment, configLocationFileStore);

        inOrder.verify(environment).getConfigDir();
        inOrder.verify(environment).getSqlUrl();
        inOrder.verify(configLocationFileStore).getAll();
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfReadingTheBootstrapFileFails() throws Exception {
        Mockito.when(environment.getConfigDir()).thenReturn(null);
        mockDefaultBootstrapFile();

        when(PropertiesReader.getProperties(any(File.class))).thenThrow(new IOException());

        bootstrapManager.loadBootstrapConfig();
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldReturnNullIfNoneOfTheFilesInTheDefaultLocationIsReadable() throws IOException {
        when(environment.getConfigDir()).thenReturn(null);
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocation1 = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocation1);
        when(configLocation1.getFile(BootstrapManager.BOOTSTRAP_PROPERTIES, READABLE)).thenThrow(new MotechConfigurationException("Failed"));
        ConfigLocation configLocation2 = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocation2);
        when(configLocation2.getFile(BootstrapManager.BOOTSTRAP_PROPERTIES, READABLE)).thenThrow(new MotechConfigurationException("Failed"));
        when(configLocationFileStore.getAll()).thenReturn(configLocationList);

        bootstrapManager.loadBootstrapConfig();
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowMotechConfigurationExceptionIfSavingBootstrapPropertiesFailed() throws IOException {
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocationMock = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocationMock);
        when(configLocationFileStore.getAll()).thenReturn(configLocationList);
        File fileMock = Mockito.mock(File.class);
        when(configLocationMock.getFile(BootstrapManager.BOOTSTRAP_PROPERTIES, WRITABLE)).thenReturn(fileMock);
        File parentDirectory = Mockito.mock(File.class);
        when(fileMock.getParentFile()).thenReturn(parentDirectory);
        Mockito.doThrow(new IOException("IO Error")).when(fileMock).createNewFile();

        bootstrapManager.saveBootstrapConfig(new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver,  "test", "test"), "test", ConfigSource.UI));
    }

    @Test
    public void shouldSaveBootstrapConfigToPropertiesFileInDefaultLocation() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, "some_username", "some_password"), "tenentId", ConfigSource.FILE);

        String tempDir = new File(System.getProperty("java.io.tmpdir"), "config").getAbsolutePath();
        List<ConfigLocation> configLocationList = new ArrayList<>();
        configLocationList.add(new ConfigLocation(tempDir));
        Mockito.when(configLocationFileStore.getAll()).thenReturn(configLocationList);

        bootstrapManager.saveBootstrapConfig(bootstrapConfig);

        Properties savedBootstrapProperties = new Properties();
        savedBootstrapProperties.load(new FileInputStream(new File(tempDir, "bootstrap.properties")));
        assertNotNull(savedBootstrapProperties);
        assertThat(savedBootstrapProperties.getProperty(SQL_URL), equalTo(sqlUrl));
        assertThat(savedBootstrapProperties.getProperty(TENANT_ID), equalTo("tenentId"));
    }
}
