package org.motechproject.server.config.service.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.repository.AllSettings;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.domain.MotechSettings;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Implementation of {@Link PlatformSettingsService} class for main motech settings managment
 */
@Service("platformSettingsService")
public class PlatformSettingsServiceImpl implements PlatformSettingsService {
/*
 *     Important Note: This class should not be developed further. Please start making
 *     future changes in org.motechproject.config.service.ConfigurationService. When Config management
 *     work completes, this class will be removed.
 */
    private static final String USER_HOME = "user.home";

    @Autowired
    private AllSettings allSettings;

    @Autowired
    private ConfigFileMonitor configFileMonitor;

    @Override
    public Properties exportPlatformSettings() {
        MotechSettings currentSettings = configFileMonitor.getCurrentSettings();
        SettingsRecord dbSettings = allSettings.getSettings();

        Properties export = new Properties();

        if (currentSettings != null) {
            export.putAll(currentSettings.getPlatformSettings());
        }

        if (dbSettings != null) {
            export.putAll(dbSettings.getActivemqProperties());
            export.put(MotechSettings.LANGUAGE, dbSettings.getLanguage());
        }

        return export;
    }


    @CacheEvict(value = {BUNDLE_CACHE_NAME }, allEntries = true)
    public void addConfigLocation(final String location) throws IOException {
        configFileMonitor.changeConfigFileLocation(location);
    }

    @Override
    @CacheEvict(value = BUNDLE_CACHE_NAME, allEntries = true)
    public void saveBundleProperties(final String bundleSymbolicName, final String fileName, final Properties properties)
            throws IOException {
        File file = new File(String.format("%s/%s", getConfigDir(bundleSymbolicName), fileName));
        setUpDirsForFile(file);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            properties.store(fileOutputStream, null);
        }
    }

    @Override
    public List<String> retrieveRegisteredBundleNames() {
        File configDir = new File(String.format(String.format("%s/.motech/config", System.getProperty(USER_HOME))));
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
    @Cacheable(value = BUNDLE_CACHE_NAME)
    public Properties getBundleProperties(final String bundleSymbolicName, final String fileName) throws IOException {
        File file = new File(String.format("%s/.motech/config/%s/%s", System.getProperty(USER_HOME), bundleSymbolicName, fileName));
        if (!file.exists()) {
            return null;
        }

        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        }
        return properties;
    }

    @Override
    @Cacheable(value = BUNDLE_CACHE_NAME)
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
    public void saveRawConfig(String bundleSymbolicName, String filename, InputStream rawData) throws IOException {
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

    @CacheEvict(value = BUNDLE_CACHE_NAME, allEntries = true)
    public void evictBundleSettingsCache() {
        // Left blank.
        // Annotation will automatically remove all cached bundle settings
    }


    private String getConfigDir(String bundleSymbolicName) {
        return String.format("%s/.motech/config/%s/", System.getProperty(USER_HOME), bundleSymbolicName);
    }

    private static void setUpDirsForFile(File file) {
        file.getParentFile().mkdirs();
    }

}
