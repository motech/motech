package org.motechproject.config.service.impl;


import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.MotechMapUtils;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.domain.ModulePropertiesRecord;
import org.motechproject.config.repository.AllModuleProperties;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.repository.AllSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Default implementation of {@link org.motechproject.config.service.ConfigurationService}.
 */
@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService {

    private static Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);
    private static final String USER_HOME = "user.home";

    @Autowired
    private ConfigFileMonitor configFileMonitor;

    @Autowired
    private CoreConfigurationService coreConfigurationService;

    @Autowired
    private AllSettings allSettings;

    @javax.annotation.Resource(name = "defaultSettings")
    private Properties defaultConfig;

    @javax.annotation.Resource(name = "defaultAnnotations")
    private Properties configAnnotation;

    @Autowired
    private AllModuleProperties allModuleProperties;

    private ConfigSource configSource;

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading bootstrap configuration.");
        }

        final BootstrapConfig bootstrapConfig = coreConfigurationService.loadBootstrapConfig();

        if (bootstrapConfig == null) {
            return null;
        }

        configSource = bootstrapConfig.getConfigSource();


        //TODO: Have a relook at this
        if (ConfigSource.FILE.equals(configSource)) {
            try {
                configFileMonitor.monitor();
            } catch (FileSystemException e) {
                logger.error("Can't start config file monitor. ", e);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("BootstrapConfig:" + bootstrapConfig);
        }

        return bootstrapConfig;
    }

    @Override
    public ConfigSource getConfigSource() {
        return configSource;
    }

    @Override
    public void save(BootstrapConfig bootstrapConfig) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving bootstrap configuration.");
        }

        coreConfigurationService.saveBootstrapConfig(bootstrapConfig);

        if (logger.isDebugEnabled()) {
            logger.debug("Saved bootstrap configuration:" + bootstrapConfig);
        }

    }

    @Override
    @Caching(cacheable = {@Cacheable(value = SETTINGS_CACHE_NAME, key = "#root.methodName")})
    public MotechSettings getPlatformSettings() {
        SettingsRecord settings = allSettings.getSettings();
        settings.mergeWithDefaults(defaultConfig);
        return settings;
    }

    @Override
    public void savePlatformSettings(Properties settings) {
        SettingsRecord dbSettings = allSettings.getSettings();

        dbSettings.setPlatformInitialized(true);
        dbSettings.setLastRun(DateTime.now());
        dbSettings.updateFromProperties(settings);

        dbSettings.removeDefaults(defaultConfig);

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            dbSettings.setConfigFileChecksum(digest.digest(dbSettings.getPlatformSettings().toString().getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new MotechException("MD5 algorithm not available", e);
        }

        allSettings.addOrUpdateSettings(dbSettings);
    }

    @Override
    public void savePlatformSettings(MotechSettings settings) {
        savePlatformSettings(settings.getPlatformSettings());
    }

    @Override
    public void setPlatformSetting(final String key, final String value) {
        SettingsRecord dbSettings = allSettings.getSettings();

        dbSettings.savePlatformSetting(key, value);

        dbSettings.removeDefaults(defaultConfig);

        allSettings.addOrUpdateSettings(dbSettings);
    }

    @Override
    public void evictMotechSettingsCache() {
        // Left blank.
        // Annotation will automatically remove all cached motech settings
    }

    @Override
    public FileInputStream createZipWithConfigFiles(String propertyFile, String fileName) throws IOException {

        File file = new File(propertyFile);
        Properties properties = allSettings.getSettings().getPlatformSettings();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(fileName));
        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        try {
            if (!properties.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();

                for (Map.Entry<Object, Object> configProperty : properties.entrySet()) {
                    stringBuilder.append("#" + configAnnotation.getProperty(configProperty.getKey().toString()) + "\n");

                    if (defaultConfig.containsKey(configProperty.getKey())
                            && !defaultConfig.getProperty(configProperty.getKey().toString()).equals("")) {
                        stringBuilder.append("#Default value:\n" + "#" + configProperty.getKey() +
                                "=" + defaultConfig.getProperty(configProperty.getKey().toString()) +
                                "\n");
                    }

                    stringBuilder.append("\n" + configProperty.getKey() + "=" + configProperty.getValue() + "\n\n");
                }

                out.write(stringBuilder.toString());
            }
        } finally {
            out.close();

            if (!properties.isEmpty()) {
                zipOutputStream.putNextEntry(new ZipEntry(propertyFile));
                IOUtils.copy(new FileInputStream(file), zipOutputStream);
                zipOutputStream.closeEntry();
            }

            zipOutputStream.close();
        }

        return new FileInputStream(fileName);
    }

    public Properties getModuleProperties(String module, String filename, Properties defaultProperties) throws IOException {
        if (ConfigSource.UI.equals(configSource)) {
            Properties properties = allModuleProperties.asProperties(module, filename);
            return properties == null && defaultProperties == null ? new Properties() :
                    MapUtils.toProperties(MotechMapUtils.mergeMaps(properties, defaultProperties));
        } else if (ConfigSource.FILE.equals(configSource)) {
            File file = new File(String.format("%s/%s", getModuleConfigDir(module), filename));
            if (!file.exists()) {
                return new Properties();
            }

            Properties properties = new Properties();
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                properties.load(fileInputStream);
            }
            return properties;
        } else {
            return defaultProperties;
        }
    }

    @Override
    public Map<String, Properties> getAllModuleProperties(String module, Map<String, Properties> allDefaultProperties) throws IOException {
        Map<String, Properties> allProperties = new HashMap<>();

        if (ConfigSource.UI.equals(configSource)) {
            List<String> filenameList = allModuleProperties.retrieveFileNamesForModule(module);
            if (filenameList == null) {
                return allDefaultProperties;
            }

            for (String filename : filenameList) {
                allProperties.put(filename, getModuleProperties(module, filename, allDefaultProperties.get(filename)));
            }
            return allProperties;
        } else if (ConfigSource.FILE.equals(configSource)) {
            File dir = new File(getModuleConfigDir(module));

            if (dir.exists()) {
                File[] files = dir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isFile() && pathname.getName().endsWith(".properties");
                    }
                });

                for (File file : files) {
                    allProperties.put(file.getName(), getModuleProperties(module, file.getName(),
                            allDefaultProperties.get(file.getName())));
                }
            }
            return allProperties;
        }

        return allDefaultProperties;
    }

    @Override
    public void addOrUpdateProperties(String module, String filename, Properties newProperties, Properties defaultProperties) throws IOException {
        Properties toPersist;
        if (ConfigSource.UI.equals(configSource)) {
            //Persist only non-default properties in database
            toPersist = new Properties();
            for (Map.Entry<Object, Object> entry : newProperties.entrySet()) {
                if (!defaultProperties.containsKey(entry.getKey()) ||
                        (!defaultProperties.get(entry.getKey()).equals(newProperties.get(entry.getKey())))) {
                    toPersist.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            toPersist = newProperties;
        }
        ModulePropertiesRecord properties = new ModulePropertiesRecord(toPersist, module, filename, false);
        allModuleProperties.addOrUpdate(properties);
    }

    @Override
    public void saveRawConfig(String module, String filename, InputStream rawData) throws IOException {
        if (ConfigSource.UI.equals(configSource)) {
            Properties p = new Properties();
            p.put("rawData", IOUtils.toString(rawData));
            ModulePropertiesRecord record = new ModulePropertiesRecord(p, module, filename, true);
            allModuleProperties.addOrUpdate(record);
        } else if (ConfigSource.FILE.equals(configSource)) {
            File file = new File(String.format("%s/raw/%s", getModuleConfigDir(module), filename));
            setUpDirsForFile(file);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.copy(rawData, fos);
            }
        }
    }

    @Override
    public List<String> retrieveRegisteredBundleNames() {
        List<String> bundleNames = new ArrayList<>();
        if (ConfigSource.UI.equals(configSource)) {
            List<ModulePropertiesRecord> allRecords = allModuleProperties.getAll();
            for (ModulePropertiesRecord rec : allRecords) {
                bundleNames.add(rec.getModule());
            }
        } else if (ConfigSource.FILE.equals(configSource)) {
            File configDir = new File(getConfigDir());
            File[] dirs = configDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });

            if (dirs != null) {
                for (File dir : dirs) {
                    bundleNames.add(dir.getName());
                }
            }
        }
        return bundleNames;
    }

    @Override
    public List<String> listRawConfigNames(String module) {
        List<String> fileNames = new ArrayList<>();
        if (ConfigSource.UI.equals(configSource)) {
            List<ModulePropertiesRecord> records = allModuleProperties.byModuleName(module);
            for (ModulePropertiesRecord rec : records) {
                if (rec.isRaw()) {
                    fileNames.add(rec.getFilename());
                }
            }
        } else if (ConfigSource.FILE.equals(configSource)) {
            File configDir = new File(getModuleConfigDir(module) + "/raw");

            File[] files = configDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isDirectory();
                }
            });

            if (files != null) {
                for (File file : files) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    @Override
    public InputStream getRawConfig(String module, String filename, Resource resource) throws IOException {
        if (ConfigSource.UI.equals(configSource)) {
            ModulePropertiesRecord rec = allModuleProperties.byModuleAndFileName(module, filename);
            if (rec.isRaw()) {
                return IOUtils.toInputStream(rec.getProperties().get("rawData"));
            } else {
                return null;
            }
        } else if (ConfigSource.FILE.equals(configSource)) {
            File file = new File(String.format("%s/raw/%s", getModuleConfigDir(module), filename));

            InputStream is = null;
            if (file.exists()) {
                is = new FileInputStream(file);
            }

            return is;
        } else {
            return resource == null ? null : resource.getInputStream();
        }
    }

    @Override
    public boolean registersProperties(String module, String filename) {
        if (ConfigSource.UI.equals(configSource)) {
            ModulePropertiesRecord rec = allModuleProperties.byModuleAndFileName(module, filename);
            return rec == null ? false : true;
        } else {
            File file = new File(String.format("%s/%s", getModuleConfigDir(module), filename));
            return file.exists();
        }
    }

    @Override
    public boolean rawConfigExists(String module, String filename) {
        if (ConfigSource.UI.equals(configSource)) {
            ModulePropertiesRecord rec = allModuleProperties.byModuleAndFileName(module, filename);
            return (rec == null) ? false : rec.isRaw();
        } else if (configSource != null && ConfigSource.FILE.equals(configSource)) {
            File file = new File(String.format("%s/raw/%s", getModuleConfigDir(module), filename));
            return file.exists();
        }
        return false;
    }

    private String getConfigDir() {
        if (configFileMonitor != null && configFileMonitor.getCurrentSettings() != null) {
            return configFileMonitor.getCurrentSettings().getFilePath();
        }
        return String.format("%s/.motech/config/", System.getProperty(USER_HOME));
    }

    private String getModuleConfigDir(String module) {
        return String.format("%s/%s/", getConfigDir(), module);
    }

    private static void setUpDirsForFile(File file) {
        file.getParentFile().mkdirs();
    }

}
