package org.motechproject.mds.testutil;

import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.util.Properties;

public class MockCoreConfigurationService implements CoreConfigurationService {

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        return null;
    }

    @Override
    public Properties loadDatanucleusConfig() {
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource(ConfigurationConstants.DATANUCLEUS_SETTINGS_FILE_NAME);
        try {
            try (InputStream is = resource.getInputStream();) {
                properties.load(is);
            }
        } catch (IOException e) {
            throw new MotechConfigurationException("Error when loading datanucleus properties");
        }

        return properties;
    }

    @Override
    public void saveBootstrapConfig(BootstrapConfig bootstrapConfig) {
    }

    @Override
    public void evictMotechCoreSettingsCache() {
    }

    @Override
    public ConfigLocation getConfigLocation() {
        return null;
    }

    @Override
    public void addConfigLocation(String location) throws FileSystemException {
    }

    @Override
    public Properties getActiveMqConfig() {
        return null;
    }
}
