package org.motechproject.config.bootstrap.impl;

import org.junit.Test;
import org.motechproject.config.bootstrap.ConfigFileReader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConfigFileReaderIT {

    @Test
    public void shouldReturnProperties() throws Exception {
        URL resource = getClass().getClassLoader().getResource("test.properties");
        String file = resource.getFile();
        ConfigFileReader configFileReader = new ConfigFileReaderImpl();
        Properties properties = configFileReader.getProperties(new File(file));
        assertNotNull(properties);
        assertThat(properties.getProperty("testkey"), is("testvalue"));
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionIfUnableToLoadTheFile() throws IOException {
        ConfigFileReader configFileReader = new ConfigFileReaderImpl();
        Properties properties = configFileReader.getProperties(new File("non_existing_filename"));
    }
}
