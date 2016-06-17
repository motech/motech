package org.motechproject.config.core.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.motechproject.config.core.bootstrap.BootstrapManager;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class ConfigPropertiesUtilsTest {

    @Test
    public void shouldCreateFileFromProperties() throws IOException {
        File file = getFile();
        Properties properties = new Properties();
        properties.put("test.one", "value1");
        properties.put("test.two", "value2");
        ConfigPropertiesUtils.saveConfig(file, properties);

        assertTrue(file.exists());

        Properties propertiesFromFile = new Properties();
        propertiesFromFile.load(new FileInputStream(file));
        assertEquals(properties, propertiesFromFile);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfNoneOfTheFilesInTheDefaultLocationIsReadable() {
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocationMock = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocationMock);
        when(configLocationMock.getLocation()).thenReturn("location");
        when(configLocationMock.getFile("fileName", ConfigLocation.FileAccessType.READABLE)).thenThrow(
                new MotechConfigurationException("File is not readable"));
        when(configLocationMock.getLocation()).thenReturn("location");
        ConfigPropertiesUtils.getDefaultPropertiesFile(ConfigLocation.FileAccessType.READABLE, configLocationList, "fileName");
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowMotechConfigurationExceptionWhenSaving() throws IOException {
        File file = Mockito.mock(File.class);
        when(file.getParentFile()).thenReturn(file);
        when(file.createNewFile()).thenThrow(new IOException());
        ConfigPropertiesUtils.saveConfig(file, new Properties());
    }

    @Test
    public void shouldReturnEmptyProperties() {
        String basePath = System.getProperty("java.io.tmpdir");
        String fileName = "fileName";

        deleteFile(new File(basePath, fileName));

        PropertiesConfiguration propertiesConfiguration = ConfigPropertiesUtils.getPropertiesConfiguration(basePath, fileName);

        assertNotNull(propertiesConfiguration);
        assertTrue(propertiesConfiguration.isEmpty());
    }

    @Test
    public void shouldReturnPropertiesWithValues() throws IOException {
        String basePath = System.getProperty("java.io.tmpdir");
        String fileName = "fileName";

        createNewFile(new File(basePath, fileName));

        PropertiesConfiguration propertiesConfiguration = ConfigPropertiesUtils.getPropertiesConfiguration(basePath, fileName);

        assertNotNull(propertiesConfiguration);
        assertFalse(propertiesConfiguration.isEmpty());

        String[] array = propertiesConfiguration.getStringArray(ConfigLocationFileStore.CONFIG_LOCATION_PROPERTY_KEY);

        assertEquals(1, array.length);
        assertEquals("/home/user/.motech/config", array[0]);
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    private void createNewFile(File file) throws IOException {
        deleteFile(file);

        file.createNewFile();

        FileUtils.writeStringToFile(file, "config.location = /home/user/.motech/config");
    }


    private File getFile() {
        String tempDir = new File(System.getProperty("java.io.tmpdir"), "config").getAbsolutePath();
        List<ConfigLocation> configLocationList = new ArrayList<>();
        configLocationList.add(new ConfigLocation(tempDir));
        File file = new File(tempDir, BootstrapManager.BOOTSTRAP_PROPERTIES);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }
}
