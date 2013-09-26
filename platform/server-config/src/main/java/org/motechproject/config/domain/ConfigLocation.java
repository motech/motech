package org.motechproject.config.domain;

import org.motechproject.config.MotechConfigurationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

public class ConfigLocation {
    private String configLocation;

    public ConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public Resource toResource() {
        if (configLocation.startsWith("/")) {
            try {
                return getUrlResource();
            } catch (MalformedURLException e) {
                throw new MotechConfigurationException(String.format("Invalid config location %s.", configLocation), e);
            }

        } else {
            return new ClassPathResource(configLocation);
        }
    }

    UrlResource getUrlResource() throws MalformedURLException {
        return new UrlResource(String.format("file:%s", configLocation));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigLocation that = (ConfigLocation) o;

        if (configLocation != null ? !configLocation.equals(that.configLocation) : that.configLocation != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return configLocation != null ? configLocation.hashCode() : 0;
    }
}
