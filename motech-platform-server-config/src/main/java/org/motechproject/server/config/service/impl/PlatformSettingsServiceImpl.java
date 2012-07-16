package org.motechproject.server.config.service.impl;

import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

@Service
public class PlatformSettingsServiceImpl implements PlatformSettingsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformSettingsServiceImpl.class);
    public static final String BUNDLE_CONFIG_FOLDER_FORMAT = "%s/.motech/config/%d/%s";

    @Autowired
    private ConfigLoader configLoader;

    @Override
    public MotechSettings getPlatformSettings() {
        return configLoader.loadConfig();
    }

    @Override
    public String getPlatformLanguage() {
        MotechSettings motechSettings = getPlatformSettings();

        return (motechSettings == null ? null : motechSettings.getLanguage());
    }

    @Override
    public String getPlatformLanguage(final String defaultValue) {
        String language = getPlatformLanguage();
        return (language == null ? defaultValue : language);
    }

    @Override
    public Locale getPlatformLocale() {
        String language = getPlatformLanguage();
        return (language == null ? Locale.getDefault() : new Locale(language));
    }

    @Override
    public void addConfigLocation(final String location, final boolean save) throws IOException {
        if (location.startsWith("/")) {
            configLoader.addConfigLocation(String.format("file:%s", location));
        } else {
            configLoader.addConfigLocation(location);
        }

        if (save) {
            configLoader.save();
        }
    }

    @Override
    public void saveBundleProperties(final Long bundleId, final String fileName, final Properties properties) throws IOException {
        File file = new File(String.format(BUNDLE_CONFIG_FOLDER_FORMAT, System.getProperty("user.home"), bundleId, fileName));

        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            LOGGER.error("Error", e);
            throw e;
        }
    }

    @Override
    public Properties getBundleProperties(final Long bundleId, final String fileName) throws IOException {
        File file = new File(String.format(BUNDLE_CONFIG_FOLDER_FORMAT, System.getProperty("user.home"), bundleId, fileName));

        if (!file.exists()) {
            return null;
        }

        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));

            return properties;
        } catch (IOException e) {
            LOGGER.error("Error", e);
            throw e;
        }
    }
}
