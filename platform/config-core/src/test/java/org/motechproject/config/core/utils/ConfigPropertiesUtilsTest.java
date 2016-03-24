package org.motechproject.config.core.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.core.bootstrap.BootstrapManager;
import org.motechproject.config.core.domain.ConfigLocation;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
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
