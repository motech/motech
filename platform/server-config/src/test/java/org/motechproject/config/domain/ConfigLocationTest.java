package org.motechproject.config.domain;

import org.junit.Test;
import org.motechproject.config.MotechConfigurationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

public class ConfigLocationTest {

    @Test
    public void shouldConvertFileLocationToResource() throws IOException {
        ConfigLocation configLocation = new ConfigLocation("/etc/motech/");

        Resource resource = configLocation.toResource();

        assertEquals("file:/etc/motech/", resource.getURL().toString());
    }

    @Test
    public void shouldConvertClasspathLocationToResource() {
        ConfigLocation configLocation = new ConfigLocation("config/");

        Resource resource = configLocation.toResource();

        assertEquals(new ClassPathResource("config/"), resource);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowMotechConfigurationExceptionWhenInvalidConfigLocationIsGiven() throws MalformedURLException {
        ConfigLocation configLocation = new ConfigLocationStub("/location");
        configLocation.toResource();
    }

    private class ConfigLocationStub extends ConfigLocation {
        public ConfigLocationStub(String configLocation) {
            super(configLocation);
        }

        @Override
        UrlResource getUrlResource() throws MalformedURLException {
            throw new MalformedURLException("Malformed");
        }
    }
}
