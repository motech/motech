package org.motechproject.server.startup;

import org.motechproject.server.startup.settings.ConfigFileSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    private List<Resource> configLocations;

    @Autowired
    private ResourceLoader resourceLoader;

    public ConfigLoader(List<Resource> configLocations) {
        this.configLocations = configLocations;
    }

    public void addConfigLocation(String location) {
        Resource resource = resourceLoader.getResource(location);
        addConfigLocation(resource);
    }

    public void addConfigLocation(Resource location) {
        if (configLocations == null) {
            configLocations = new ArrayList<>();
        }
        configLocations.add(location);
    }

    public ConfigFileSettings loadConfig() {
        ConfigFileSettings configFileSettings = null;

        if (configLocations != null) {
            for (Resource location : configLocations) {
                if (location.isReadable()) {
                    try {
                        configFileSettings = loadSettingsFromStream(location.getInputStream());
                        configFileSettings.setFileURL(location.getURL());
                        break; // startup found
                    } catch (IOException e) {
                        logger.error("Error loading config " + location.getFilename(), e);
                    }
                }
            }
        }

        return configFileSettings;
    }
    
    public static ConfigFileSettings loadSettingsFromStream(InputStream is) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            try (DigestInputStream dis = new DigestInputStream(is, digest)) {
                //load configFileSettings and calculate MD5 hash
                ConfigFileSettings configFileSettings = new ConfigFileSettings();
                configFileSettings.load(dis);

                return configFileSettings; // startup loaded
            } catch (IOException e) {
                logger.error("Error loading configuration", e);
                throw new RuntimeException(e);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5 algorithm not available", e);
            throw new RuntimeException(e);
        }
    }
}
