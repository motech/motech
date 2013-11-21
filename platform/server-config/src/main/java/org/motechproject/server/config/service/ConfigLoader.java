package org.motechproject.server.config.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.filestore.ConfigFileFilter;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.LoginMode;
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

import static org.motechproject.config.core.constants.ConfigurationConstants.AMQ_BROKER_URL;

/**
 * Config loader used to load the platform core settings.
 */
@Component
public class ConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired(required = false)
    private EventAdmin eventAdmin;

    @Autowired
    private CoreConfigurationService coreConfigurationService;

    @Autowired
    private ConfigurationService configurationService;

    public SettingsRecord loadMotechSettings() {
        SettingsRecord settingsRecord;
        ConfigLocation configLocation = coreConfigurationService.getConfigLocation();
        Resource configLocationResource = configLocation.toResource();
        try {
            Resource motechSettings = configLocationResource.createRelative(ConfigurationConstants.SETTINGS_FILE_NAME);
            settingsRecord = loadSettingsFromStream(motechSettings);
            settingsRecord.setFilePath(configLocationResource.getURL().getPath());
            checkSettingsRecord(settingsRecord);

            if (eventAdmin != null) {
                Map<String, String> properties = new HashMap<>();
                Properties activemqProperties = settingsRecord.getActivemqProperties();
                if (activemqProperties != null && activemqProperties.containsKey(AMQ_BROKER_URL)) {
                    properties.put(AMQ_BROKER_URL, activemqProperties.getProperty(AMQ_BROKER_URL));
                    eventAdmin.postEvent(new Event("org/motechproject/osgi/event/RELOAD", properties));
                }
            }
        } catch (IOException e) {
            throw new MotechConfigurationException(String.format("Could not read settings from file at location %s", configLocation), e);
        }
        return settingsRecord;
    }

    /**
     * Loads all configs from the config location and adds or updates the records in database.
     *
     * @throws IOException If there is any error while handling the files.
     */
    public void processExistingConfigs() throws IOException {
        final ConfigLocation currentConfigLocation = coreConfigurationService.getConfigLocation();
        String location = currentConfigLocation.getLocation();

        List<File> files = (List<File>) FileUtils.listFiles(new File(location), new ConfigFileFilter(), TrueFileFilter.INSTANCE);
        LOGGER.debug(String.format("Processing existing files. %s", files));
        configurationService.addOrUpdate(files);
    }

    private void checkSettingsRecord(SettingsRecord settingsRecord) {
        LoginMode loginMode = settingsRecord.getLoginMode();
        if (loginMode == null || (!loginMode.isRepository() && !loginMode.isOpenId())) {
            throw new MotechConfigurationException("Login mode has an incorrect value. Acceptable values: \"repository\", \"openId\".");
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
