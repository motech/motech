package org.motechproject.config.loader;

import org.motechproject.commons.api.MotechException;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.domain.LoginMode;
import org.motechproject.config.domain.SettingsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.List;

/**
 * Config loader used to load the platform core settings.
 */
@Component
public class ConfigLoader {
    private ResourceLoader resourceLoader;
    private CoreConfigurationService coreConfigurationService;

    /**
     * Loads MOTECH settings containing core platform settings.
     *
     * @return the {SettingsRecord} object
     */
    public SettingsRecord loadMotechSettings() {
        SettingsRecord settingsRecord;
        ConfigLocation configLocation = coreConfigurationService.getConfigLocation();
        Resource configLocationResource = configLocation.toResource();
        try {
            Resource motechSettings = configLocationResource.createRelative(ConfigurationConstants.SETTINGS_FILE_NAME);
            settingsRecord = loadSettingsFromStream(motechSettings);
            settingsRecord.setFilePath(configLocationResource.getURL().getPath());
            checkSettingsRecord(settingsRecord);
        } catch (IOException e) {
            throw new MotechConfigurationException(String.format("Could not read settings from file at location %s", configLocation), e);
        }
        return settingsRecord;
    }

    /**
     * Finds all configurations from the configuration location.
     *
     * @throws IOException If there is any error while handling the files.
     */
    public List<File> findExistingConfigs() throws IOException {
        final ConfigLocation currentConfigLocation = coreConfigurationService.getConfigLocation();
        return currentConfigLocation.getExistingConfigFiles();
    }

    private void checkSettingsRecord(SettingsRecord settingsRecord) {
        LoginMode loginMode = settingsRecord.getLoginMode();
        if (loginMode == null || (!loginMode.isRepository() && !loginMode.isOpenId())) {
            throw new MotechConfigurationException("Login mode has an incorrect value. Acceptable values: \"repository\", \"openId\".");
        }
    }

    /**
     * Loads default MOTECH settings.
     *
     * @return the {SettingsRecord} object
     */
    public SettingsRecord loadDefaultConfig() {
        SettingsRecord settingsRecord = null;
        Resource defaultSettings = resourceLoader.getResource("classpath:motech-settings.properties");
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
                settingsRecord.setConfigFileChecksum(new String(digest.digest()));
                return settingsRecord; // startup loaded
            } catch (IOException e) {
                throw new MotechException("Error loading configuration", e);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new MotechException("MD5 algorithm not available", e);
        }
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Autowired
    public void setCoreConfigurationService(CoreConfigurationService coreConfigurationService) {
        this.coreConfigurationService = coreConfigurationService;
    }
}
