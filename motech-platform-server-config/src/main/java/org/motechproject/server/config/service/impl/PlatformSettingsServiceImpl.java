package org.motechproject.server.config.service.impl;

import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.db.CouchDbManager;
import org.motechproject.server.config.db.DbConnectionException;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.service.AllSettings;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Service
public class PlatformSettingsServiceImpl implements PlatformSettingsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformSettingsServiceImpl.class);

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private CouchDbManager couchDbManager;

    @Override
    public MotechSettings getPlatformSettings() {
        MotechSettings settings = configLoader.loadConfig();
        SettingsRecord record;

        try {
            record = getDBSettings();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            record = null;
        }

        if (record != null) {
            record.setCouchDbProperties(settings.getCouchDBProperties());
            settings = record;
        }

        return settings;
    }

    @Override
    public void setPlatformSetting(final String key, final String value) {
        ConfigFileSettings configFileSettings = configLoader.loadConfig();
        File configFile = new File(configFileSettings.getPath());

        try {
            // save property to config file
            if (configFile.canWrite()) {
                if (configFileSettings.containsKey(key)) {
                    configFileSettings.put(key, value);
                    configFileSettings.store(new FileOutputStream(configFile), null);
                }
            }

            // save property to db
            SettingsRecord dbSettings = getDBSettings();

            if (MotechSettings.LANGUAGE.equals(key)) {
                dbSettings.setLanguage(value);
            } else {
                for (Properties p : Arrays.asList(dbSettings.getActivemqProperties(), dbSettings.getQuartzProperties())) {
                    if (p.containsKey(key)) {
                        p.put(key, value);

                        break;
                    }
                }
            }

            saveDBSettings(dbSettings);
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
        }
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
    public Properties exportPlatformSettings() {
        ConfigFileSettings configFileSettings = configLoader.loadConfig();
        SettingsRecord dbSettings;

        try {
            dbSettings = getDBSettings();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            dbSettings = null;
        }

        Properties export = new Properties();

        if (configFileSettings != null) {
            export.putAll(configFileSettings);
        }

        if (dbSettings != null) {
            export.putAll(dbSettings.getActivemqProperties());
            export.putAll(dbSettings.getQuartzProperties());
            export.put(MotechSettings.LANGUAGE, dbSettings.getLanguage());
        }

        return export;
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
    public void saveBundleProperties(final String bundleSymbolicName, final String fileName, final Properties properties) throws IOException {
        File file = new File(String.format("%s/.motech/config/%s/%s", System.getProperty("user.home"), bundleSymbolicName, fileName));

        properties.store(new FileOutputStream(file), null);
    }

    @Override
    public Properties getBundleProperties(final String bundleSymbolicName, final String fileName) throws IOException {
        File file = new File(String.format("%s/.motech/config/%s/%s", System.getProperty("user.home"), bundleSymbolicName, fileName));

        if (!file.exists()) {
            return null;
        }

        Properties properties = new Properties();
        properties.load(new FileInputStream(file));

        return properties;
    }

    @Override
    public Map<String, Properties> getAllProperties(final String bundleSymbolicName) throws IOException {
        File dir = new File(String.format("%s/.motech/config/%s/", System.getProperty("user.home"), bundleSymbolicName));
        Map<String, Properties> propertiesMap = null;

        if (dir.exists()) {
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().endsWith(".properties");
                }
            });

            propertiesMap = new HashMap<>(files.length);

            for (File file : files) {
                propertiesMap.put(file.getName(), getBundleProperties(bundleSymbolicName, file.getName()));
            }
        }

        return propertiesMap;
    }

    private SettingsRecord getDBSettings() throws DbConnectionException {
        ConfigFileSettings configFileSettings = configLoader.loadConfig();
        SettingsRecord record = null;

        if (configFileSettings != null) {
            couchDbManager.configureDb(configFileSettings.getCouchDBProperties());
            AllSettings allSettings = new AllSettings(couchDbManager.getConnector(SETTINGS_DB, true));

            record = allSettings.getSettings();
        }

        return record;
    }

    private void saveDBSettings(final SettingsRecord settingsRecord) throws DbConnectionException {
        ConfigFileSettings configFileSettings = configLoader.loadConfig();

        if (configFileSettings != null) {
            couchDbManager.configureDb(configFileSettings.getCouchDBProperties());
            AllSettings allSettings = new AllSettings(couchDbManager.getConnector(SETTINGS_DB, true));

            allSettings.addOrUpdateSettings(settingsRecord);
        }
    }

}
