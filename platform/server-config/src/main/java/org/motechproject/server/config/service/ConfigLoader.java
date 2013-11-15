package org.motechproject.server.config.service;

import org.apache.commons.io.FileUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Config loader used to load the platform core settings.
 */
@Component
public class ConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String BROKER_URL = "jms.broker.url";
    private static final String[] IGNORE_FILE_LIST = new String[]{"bootstrap.properties", "motech-settings.conf"};
    private static final String[] SUPPORTED_FILE_EXTNS = new String[]{"properties", "json"};

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired(required = false)
    private EventAdmin eventAdmin;

    @Autowired
    private CoreConfigurationService coreConfigurationService;

    @Autowired
    private ConfigurationService configurationService;

    public SettingsRecord loadConfig() {
        SettingsRecord settingsRecord = null;

        Iterable<ConfigLocation> configLocations = coreConfigurationService.getConfigLocations();
        for (ConfigLocation configLocation : configLocations) {
            Resource configLocationResource = configLocation.toResource();
            try {
                Resource motechSettings = configLocationResource.createRelative(MotechSettings.SETTINGS_FILE_NAME);
                if (!motechSettings.isReadable()) {
                    LOGGER.warn("Could not read motech-settings.conf from: " + configLocationResource.toString());
                    continue;
                }

                settingsRecord = loadSettingsFromStream(motechSettings);
                settingsRecord.setFilePath(configLocationResource.getURL().getPath());

                if (eventAdmin != null) {
                    Map<String, String> properties = new HashMap<>();
                    Properties activemqProperties = settingsRecord.getActivemqProperties();
                    if (activemqProperties != null && activemqProperties.containsKey(BROKER_URL)) {
                        properties.put(BROKER_URL, activemqProperties.getProperty(BROKER_URL));
                        eventAdmin.postEvent(new Event("org/motechproject/osgi/event/RELOAD", properties));
                    }
                }
                configLocation.markAsCurrentLocation();
                break;
            } catch (IOException e) {
                LOGGER.warn("Problem reading motech-settings.conf from location: " + configLocationResource.toString(), e);
            }
        }

        checkSettingsRecord(settingsRecord);

        return settingsRecord;
    }

    findConfigLocation() {
        iter - all configurationService and getCurrentConfigLocation() the location
    }

    //TODO: Relook at calling loadConfig()
    public ConfigLocation getCurrentConfigLocation() {
        for (ConfigLocation configLocation : coreConfigurationService.getConfigLocations()) {
            if (configLocation.isCurrentLocation()) {
                return configLocation;
            }
        }
        return null;
    }

    /**
     * Loads all configs from the config location and adds or updates the records in database.
     * @throws IOException If there is any error while handling the files.
     */
    public void processExistingConfigs() throws IOException {
        final ConfigLocation currentConfigLocation = getCurrentConfigLocation();
        if(currentConfigLocation == null) {
            loadConfig();
        }
        String location = currentConfigLocation.getLocation();

        File dir = new File(location);
        List<File> files = (List<File>) FileUtils.listFiles(dir, SUPPORTED_FILE_EXTNS, true);
        configurationService.addOrUpdateProperties(files);
    }

    private void checkSettingsRecord(SettingsRecord settingsRecord) {
        if (settingsRecord == null) {
            throw new MotechConfigurationException("Could not read settings from file");
        } else {
            LoginMode loginMode = settingsRecord.getLoginMode();
            if (loginMode == null || (!loginMode.isRepository() && !loginMode.isOpenId())) {
                throw new MotechConfigurationException("Login mode has an incorrect value. Acceptable values: \"repository\", \"openId\".");
            }
        }
    }

    public SettingsRecord loadDefaultConfig() {
        SettingsRecord settingsRecord = null;
        Resource defaultSettings = resourceLoader.getResource("classpath:motech-settings.conf");
        if (defaultSettings != null) {
            settingsRecord = loadSettingsFromStream(defaultSettings);
        }

        return settingsRecord;
    }

    private SettingsRecord loadSettingsFromStream(Resource motechSettings) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            try (DigestInputStream dis = new DigestInputStream(motechSettings.getInputStream(), digest)) {
                //load configFileSettings and calculate MD5 hash
                SettingsRecord settingsRecord = new SettingsRecord();
                settingsRecord.load(dis);
                settingsRecord.setConfigFileChecksum(digest.digest());
                return settingsRecord; // startup loaded
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
}
