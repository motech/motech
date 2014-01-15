package org.motechproject.config.core.filestore;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PropertiesReaderIT {

    @Test
    public void shouldReturnProperties() throws Exception {
        URL resource = getClass().getClassLoader().getResource("test.properties");
        String file = resource.getFile();
        Properties properties = PropertiesReader.getProperties(new File(file));
        assertNotNull(properties);
        assertThat(properties.getProperty("testkey"), Is.is("testvalue"));
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionIfUnableToLoadTheFile() throws IOException {
        Properties properties = PropertiesReader.getProperties(new File("non_existing_filename"));
    }
}
