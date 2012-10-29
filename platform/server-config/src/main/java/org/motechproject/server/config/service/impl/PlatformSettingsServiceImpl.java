package org.motechproject.server.config.service.impl;

import org.apache.commons.io.IOUtils;
import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.motechproject.server.config.db.CouchDbManager;
import org.motechproject.server.config.db.DbConnectionException;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.service.AllSettings;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Service("platformSettingsService")
public class PlatformSettingsServiceImpl implements PlatformSettingsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformSettingsServiceImpl.class);

    private AllSettings allSettings;

    @Autowired
    private CouchDbManager couchDbManager;

    @Autowired
    private ConfigFileMonitor configFileMonitor;

    @Override
    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    public void configureCouchDBManager() throws DbConnectionException {
        ConfigFileSettings configFileSettings = configFileMonitor.getCurrentSettings();

        if (configFileSettings != null && !configFileSettings.getCouchDBProperties().isEmpty()) {
            couchDbManager.configureDb(configFileSettings.getCouchDBProperties());
            allSettings = new AllSettings(getCouchConnector(SETTINGS_DB));
        }
    }

    @Override
    @Cacheable(value = SETTINGS_CACHE_NAME, key = "#root.methodName")
    public MotechSettings getPlatformSettings() {
        MotechSettings settings = configFileMonitor.getCurrentSettings();

        if (settings != null) {
            SettingsRecord record = getDBSettings();

            if (record != null) {
                record.setCouchDbProperties(settings.getCouchDBProperties());
                settings = record;
            }
        }

        return settings;
    }

    @Override
    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    public void savePlatformSettings(Properties settings) {
        createConfigDir();

        File file = new File(String.format("%s/.motech/config/%s", System.getProperty("user.home"), SETTINGS_FILE_NAME));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            settings.store(fos, null);

            SettingsRecord dbSettings = getDBSettings();
            if (dbSettings == null) {
                dbSettings = new SettingsRecord();
                configureCouchDBManager();
            }

            if (allSettings != null) {
                dbSettings.updateFromProperties(settings);
                dbSettings.setConfigFileChecksum(configFileMonitor.getCurrentSettings().getMd5checkSum());
                dbSettings.setLastRun(DateTime.now());

                allSettings.addOrUpdateSettings(dbSettings);
            }

            configFileMonitor.monitor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @CacheEvict(value = ACTIVEMQ_CACHE_NAME, allEntries = true)
    public void saveActiveMqSettings(Properties settings) {
        createConfigDir();

        File file = new File(String.format("%s/.motech/config/%s", System.getProperty("user.home"), ACTIVEMQ_FILE_NAME));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            settings.store(fos, null);
            configFileMonitor.monitor();

            SettingsRecord dbSettings = getDBSettings();
            if (dbSettings == null) {
                LOGGER.warn("activemq properties cannot be saved to database");
                return;
            }
            dbSettings.setActivemqProperties(settings);
            allSettings.addOrUpdateSettings(dbSettings);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    public void setPlatformSetting(final String key, final String value) {
        ConfigFileSettings configFileSettings = configFileMonitor.getCurrentSettings();

        if (configFileSettings == null) {
            // init settings
            savePlatformSettings(new Properties());
            configFileSettings = configFileMonitor.getCurrentSettings();
        }

        File configFile = new File(configFileSettings.getPath() + File.separator + SETTINGS_FILE_NAME);

        try {
            // save property to config file
            if (configFile.canWrite()) {
                Properties couchDb = configFileSettings.getCouchDBProperties();

                configFileSettings.saveMotechSetting(key, value);
                configFileSettings.storeMotechSettings();

                if (!configFileSettings.getCouchDBProperties().equals(couchDb)) {
                    configureCouchDBManager();
                }
            }

            // save property to db
            SettingsRecord dbSettings = getDBSettings();

            if (dbSettings != null) {
                if (MotechSettings.LANGUAGE.equals(key)) {
                    dbSettings.setLanguage(value);
                } else if (MotechSettings.STATUS_MSG_TIMEOUT.equals(key)) {
                    dbSettings.setStatusMsgTimeout(value);
                } else {
                    for (Properties p : Arrays.asList(dbSettings.getQuartzProperties(),
                            dbSettings.getMetricsProperties())) {
                        if (p.containsKey(key)) {
                            p.put(key, value);

                            break;
                        }
                    }
                }

                allSettings.addOrUpdateSettings(dbSettings);
            }
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
        }
    }

    @Override
    @CacheEvict(value = ACTIVEMQ_CACHE_NAME, allEntries = true)
    public void setActiveMqSetting(String key, String value) {
        ConfigFileSettings configFileSettings = configFileMonitor.getCurrentSettings();

        if (configFileSettings == null) {
            LOGGER.error("Cannot save active mq settings because motech settings file does not exist");
            return;
        }

        File configFile = new File(configFileSettings.getPath() + File.separator + ACTIVEMQ_FILE_NAME);
        try {
            if (configFile.canWrite()) {
                configFileSettings.saveActiveMqSetting(key, value);
                configFileSettings.storeActiveMqSettings();
            }

            // save property to db
            SettingsRecord dbSettings = getDBSettings();

            if (dbSettings != null) {
                dbSettings.getActivemqProperties().setProperty(key, value);
                allSettings.addOrUpdateSettings(dbSettings);
            }
        } catch (Exception e) {
            LOGGER.error("There was a problem updating an activemq setting");
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
        ConfigFileSettings configFileSettings = configFileMonitor.getCurrentSettings();
        SettingsRecord dbSettings = getDBSettings();

        Properties export = new Properties();

        if (configFileSettings != null) {
            export.putAll(configFileSettings.getAll());
        }

        if (dbSettings != null) {
            export.putAll(dbSettings.getActivemqProperties());
            export.putAll(dbSettings.getQuartzProperties());
            export.put(MotechSettings.LANGUAGE, dbSettings.getLanguage());
        }

        return export;
    }

    @CacheEvict(value = { SETTINGS_CACHE_NAME, ACTIVEMQ_CACHE_NAME }, allEntries = true)
    public void addConfigLocation(final String location, final boolean save) throws Exception {
        configFileMonitor.changeConfigFileLocation(location, save);
    }

    @Override
    public void saveBundleProperties(final String bundleSymbolicName, final String fileName, final Properties properties) throws IOException {
        File file = new File(String.format("%s/%s", getConfigDir(bundleSymbolicName), fileName));
        setUpDirsForFile(file);

        properties.store(new FileOutputStream(file), null);
    }

    @Override
    public List<String> retrieveRegisteredBundleNames() {
        File configDir = new File(String.format(String.format("%s/.motech/config", System.getProperty("user.home"))));
        File[] dirs = configDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        List<String> bundleNames = new ArrayList<>();
        if (dirs != null) {
            for (File dir : dirs) {
                bundleNames.add(dir.getName());
            }
        }

        return bundleNames;
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
        File dir = new File(getConfigDir(bundleSymbolicName));
        Map<String, Properties> propertiesMap = new HashMap<>();

        if (dir.exists()) {
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().endsWith(".properties");
                }
            });

            for (File file : files) {
                propertiesMap.put(file.getName(), getBundleProperties(bundleSymbolicName, file.getName()));
            }
        }

        return propertiesMap;
    }

    @Override
    public void saveRawConfig(String bundleSymbolicName, String filename, InputStream rawData) throws  IOException {
        File file = new File(String.format("%s/raw/%s", getConfigDir(bundleSymbolicName), filename));
        setUpDirsForFile(file);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            IOUtils.copy(rawData, fos);
        }
    }

    @Override
    public InputStream getRawConfig(String bundleSymbolicName, String filename) throws IOException {
        File file = new File(String.format("%s/raw/%s", getConfigDir(bundleSymbolicName), filename));

        InputStream is = null;
        if (file.exists()) {
            is = new FileInputStream(file);
        }

        return is;
    }

    @Override
    public boolean rawConfigExists(String bundleSymbolicName, String filename) {
        File file = new File(String.format("%s/raw/%s", getConfigDir(bundleSymbolicName), filename));
        return file.exists();
    }

    @Override
    public List<String> listRawConfigNames(String bundleSymbolicName) {
        File configDir = new File(getConfigDir(bundleSymbolicName) + "/raw");

        File[] files = configDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory();
            }
        });

        List<String> result = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                result.add(file.getName());
            }
        }

        return result;
    }

    @Override
    public CouchDbConnector getCouchConnector(String dbName) {
        return couchDbManager.getConnector(dbName, true);
    }

    @Override
    @CacheEvict(value = { SETTINGS_CACHE_NAME, ACTIVEMQ_CACHE_NAME }, allEntries = true)
    public void evictMotechSettingsCache() {
        // Left blank.
        // Annotation will automatically remove all cached motech settings
    }

    private String getConfigDir(String bundleSymbolicName) {
        return String.format("%s/.motech/config/%s/", System.getProperty("user.home"), bundleSymbolicName);
    }

    private void createConfigDir() {
        File dir = new File(String.format("%s/.motech/config", System.getProperty("user.home")));
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static void setUpDirsForFile(File file) {
        file.getParentFile().mkdirs();
    }

    private SettingsRecord getDBSettings() {
        if (allSettings == null) {
            try {
                configureCouchDBManager();
            } catch (DbConnectionException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return allSettings == null ? null : allSettings.getSettings();
    }

    @Override
    @Cacheable(value = ACTIVEMQ_CACHE_NAME, key = "#root.methodName")
    public Properties getActiveMqProperties() {
        return getPlatformSettings().getActivemqProperties();
    }

}
