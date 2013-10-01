package org.motechproject.server.config.service;

import org.motechproject.commons.api.MotechException;
import org.motechproject.config.domain.ConfigLocation;
import org.motechproject.config.filestore.ConfigLocationFileStore;
import org.motechproject.server.config.domain.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Config loader used to load the platform core settings.
 */
@Component
public class ConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String BROKER_URL = "broker.url";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired(required = false)
    private EventAdmin eventAdmin;

    @Autowired
    private ConfigLocationFileStore configLocationFileStore;

    public ConfigFileSettings loadConfig() {
        ConfigFileSettings configFileSettings = null;

        Iterable<ConfigLocation> configLocations = configLocationFileStore.getAll();
        for (ConfigLocation configLocation : configLocations) {
            Resource configLocationResource = configLocation.toResource();
            try {
                Resource motechSettings = configLocationResource.createRelative(MotechSettings.SETTINGS_FILE_NAME);
                if (!motechSettings.isReadable()) {
                    LOGGER.warn("Could not read motech-settings.conf from: " + configLocationResource.toString());
                    continue;
                }

                Resource activemq = configLocationResource.createRelative(MotechSettings.ACTIVEMQ_FILE_NAME);
                if (!activemq.isReadable()) {
                    LOGGER.warn("No activemq.properties file found at: " + configLocationResource.toString());
                    LOGGER.warn("Using default activemq.properties file");
                    activemq = resourceLoader.getResource("classpath:default-activemq.properties");
                }

                configFileSettings = loadSettingsFromStream(motechSettings, activemq);
                configFileSettings.setFileURL(configLocationResource.getURL());
                if (eventAdmin != null) {
                    Map<String, String> properties = new HashMap<>();
                    Properties activemqProperties = configFileSettings.getActivemqProperties();
                    if (activemqProperties != null && activemqProperties.containsKey(BROKER_URL)) {
                        properties.put(BROKER_URL, activemqProperties.getProperty(BROKER_URL));
                        eventAdmin.postEvent(new Event("org/motechproject/osgi/event/RELOAD", properties));
                    }
                }
                break;
            } catch (IOException e) {
                LOGGER.warn("Problem reading motech-settings.conf from location: " + configLocationResource.toString());
                continue;
            }
        }

        return configFileSettings;
    }

    public ConfigFileSettings loadDefaultConfig() {
        ConfigFileSettings configFileSettings = null;
        Resource defaultSettings = resourceLoader.getResource("classpath:default-settings.conf");
        Resource defaultActivemq = resourceLoader.getResource("classpath:default-activemq.properties");
        if (defaultSettings != null) {
            configFileSettings = loadSettingsFromStream(defaultSettings, defaultActivemq);
        }

        return configFileSettings;
    }

    private ConfigFileSettings loadSettingsFromStream(Resource motechSettings, Resource activemq) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            try (DigestInputStream dis = new DigestInputStream(motechSettings.getInputStream(), digest);
                 DigestInputStream dis2 = new DigestInputStream(activemq.getInputStream(), digest)) {
                //load configFileSettings and calculate MD5 hash
                ConfigFileSettings configFileSettings = new ConfigFileSettings();
                configFileSettings.load(dis);
                configFileSettings.loadActiveMq(dis2);
                configFileSettings.setMd5checksum(digest.digest());
                return configFileSettings; // startup loaded
            } catch (IOException e) {
                throw new MotechException("Error loading configuration", e);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new MotechException("MD5 algorithm not available", e);
        }
    }

    void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    void setConfigLocationFileStore(ConfigLocationFileStore configLocationFileStore) {
        this.configLocationFileStore = configLocationFileStore;
    }
}
