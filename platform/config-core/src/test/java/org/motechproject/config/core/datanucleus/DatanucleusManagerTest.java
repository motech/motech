package org.motechproject.config.core.datanucleus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.datanucleus.impl.DatanucleusManagerImpl;
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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class DatanucleusManagerTest {

    DatanucleusManager datanucleusManager;

    @Mock
    Environment environment;

    @Mock
    private ConfigLocationFileStore configLocationFileStore;

    @Before
    public void setUp() {
        datanucleusManager = new DatanucleusManagerImpl();
        ((DatanucleusManagerImpl)datanucleusManager).setConfigLocationFileStore(configLocationFileStore);
        ((DatanucleusManagerImpl)datanucleusManager).setEnvironment(environment);
    }

    @Test
    public void shouldCopyFileWhenLoadingPropertiesFromClassPath() throws IOException {
        when(environment.getDatanucleusProperties()).thenReturn(new Properties());
        when(environment.getConfigDir()).thenReturn("");

        String tempDir = new File(System.getProperty("java.io.tmpdir"), "config").getAbsolutePath();
        List<ConfigLocation> configLocationList = new ArrayList<>();
        File file = new File(tempDir, ConfigurationConstants.DATANUCLEUS_SETTINGS_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }

        configLocationList.add(new ConfigLocation(tempDir));
        when(configLocationFileStore.getAll()).thenReturn(configLocationList);

        datanucleusManager.getDatanucleusProperties();

        Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream(file)) {
            properties.load(is);
        }

        assertEquals(getCompleteProperties(), properties);
    }

    @Test
    public void shouldGetPropertiesFromEnvironmet() throws IOException {
        when(environment.getDatanucleusProperties()).thenReturn(getCompleteProperties());
        when(environment.getConfigDir()).thenReturn("");

        List<ConfigLocation> configLocationList = new ArrayList<>();
        when(configLocationFileStore.getAll()).thenReturn(configLocationList);

        datanucleusManager.getDatanucleusProperties();
        verify(configLocationFileStore, times(0)).getAll();
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test
    public void shouldReturnConfigFromFileSpecifiedInTheEnvironmentVariable() throws IOException {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        when(environment.getConfigDir()).thenReturn("file_location");
        when( environment.getBootstrapPropperties()).thenReturn(getCompleteProperties());

        Properties properties = getCompleteProperties();
        Mockito.when(ConfigPropertiesUtils.getPropertiesFromFile(new File("file_location",
                ConfigurationConstants.DATANUCLEUS_SETTINGS_FILE_NAME))).thenReturn(properties);

        assertEquals(getCompleteProperties(), datanucleusManager.getDatanucleusProperties());
    }

    @Test
    public void shouldLoadPropertiesFromDefaultConfigLocation() throws IOException {
        when(environment.getDatanucleusProperties()).thenReturn(new Properties());
        when(environment.getConfigDir()).thenReturn("");

        String tempDir = new File(System.getProperty("java.io.tmpdir"), "config").getAbsolutePath();
        List<ConfigLocation> configLocationList = new ArrayList<>();
        File file = new File(tempDir, ConfigurationConstants.DATANUCLEUS_SETTINGS_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        Properties properties = getCompleteProperties();
        try (FileOutputStream os = new FileOutputStream(file)) {
            properties.store(os, null);
        }

        configLocationList.add(new ConfigLocation(tempDir));
        when(configLocationFileStore.getAll()).thenReturn(configLocationList);

        datanucleusManager.getDatanucleusProperties();

        assertEquals(getCompleteProperties(), properties);
    }

    private Properties getCompleteProperties() throws IOException {
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource(ConfigurationConstants.DATANUCLEUS_SETTINGS_FILE_NAME);
        try (InputStream is = resource.getInputStream();) {
            properties.load(is);
        }
        return properties;
    }
}
