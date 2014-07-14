package org.motechproject.config.service.impl;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.MotechMapUtils;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.domain.ModulePropertiesRecord;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.config.service.ModulePropertiesService;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.service.ConfigLoader;
import org.motechproject.server.config.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.motechproject.config.core.filters.ConfigFileFilter.isPlatformCoreConfigFile;

/**
 * Default implementation of {@link org.motechproject.config.service.ConfigurationService}.
 */
@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final String STRING_FORMAT = "%s/%s";
    private static Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);

    private ConfigLoader configLoader;
    private ConfigSource configSource;
    private ResourceLoader resourceLoader;
    private CoreConfigurationService coreConfigurationService;
    private ModulePropertiesService modulePropertiesService;
    private SettingService settingService;

    private Properties defaultConfig;
    private Properties configAnnotation;

    public ConfigurationServiceImpl() {
    }

    @Autowired
    public ConfigurationServiceImpl(CoreConfigurationService coreConfigurationService,
                                    SettingService settingService, ModulePropertiesService modulePropertiesService,
                                    ConfigLoader configLoader, ResourceLoader resourceLoader) {
        this.coreConfigurationService = coreConfigurationService;
        this.settingService = settingService;
        this.configLoader = configLoader;
        this.resourceLoader = resourceLoader;
        this.modulePropertiesService = modulePropertiesService;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading bootstrap configuration.");
        }

        final BootstrapConfig bootstrapConfig;

        try {
            bootstrapConfig = coreConfigurationService == null ? null : coreConfigurationService.loadBootstrapConfig();
        } catch (MotechConfigurationException e) {
            return null;
        }

        if (null != bootstrapConfig) {
            configSource = bootstrapConfig.getConfigSource();
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
    @Caching(cacheable = {@Cacheable(value = SETTINGS_CACHE_NAME, key = "#root.methodName") })
    public MotechSettings getPlatformSettings() {
        if (settingService == null) {
            return null;
        }
        SettingsRecord settings = getSettings();
        settings.mergeWithDefaults(defaultConfig);
        return settings;
    }

    @Override
    public void savePlatformSettings(Properties settings) {
        SettingsRecord dbSettings = getSettings();

        dbSettings.setPlatformInitialized(true);
        dbSettings.setLastRun(DateTime.now());
        dbSettings.updateFromProperties(settings);

        dbSettings.removeDefaults(defaultConfig);

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            dbSettings.setConfigFileChecksum(new String(digest.digest(
                    MapUtils.toProperties(dbSettings.asProperties()).toString().getBytes())));
        } catch (NoSuchAlgorithmException e) {
            throw new MotechException("MD5 algorithm not available", e);
        }

        addOrUpdateSettings(dbSettings);
    }

    @Override
    public void savePlatformSettings(MotechSettings settings) {
        savePlatformSettings(settings.asProperties());
    }

    @Override
    public void setPlatformSetting(final String key, final String value) {
        SettingsRecord dbSettings = getSettings();

        dbSettings.savePlatformSetting(key, value);

        dbSettings.removeDefaults(defaultConfig);

        addOrUpdateSettings(dbSettings);
    }

    @Override
    public void evictMotechSettingsCache() {
        // Left blank.
        // Annotation will automatically remove all cached motech settings
    }

    @Override
    public FileInputStream createZipWithConfigFiles(String propertyFile, String fileName) throws IOException {

        File file = new File(propertyFile);
        Properties properties = getSettings().asProperties();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(fileName));

        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            if (!properties.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();

                for (Map.Entry<Object, Object> configProperty : properties.entrySet()) {
                    stringBuilder.append("#")
                            .append(configAnnotation.getProperty(configProperty.getKey().toString()))
                            .append("\n");

                    if (defaultConfig.containsKey(configProperty.getKey())
                            && !"".equals(defaultConfig.getProperty(configProperty.getKey().toString()))) {
                        stringBuilder.append("#Default value:\n" + "#")
                                .append(configProperty.getKey())
                                .append("=")
                                .append(defaultConfig.getProperty(configProperty.getKey().toString()))
                                .append("\n");
                    }

                    stringBuilder.append("\n")
                            .append(configProperty.getKey())
                            .append("=")
                            .append(configProperty.getValue())
                            .append("\n\n");
                }

                out.write(stringBuilder.toString());
            }
        } finally {
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
        ModulePropertiesRecord record;
        Properties properties;

        record = getModulePropertiesRecord(module, filename);
        if (record != null) {
            properties = MapUtils.toProperties(record.getProperties());
        } else {
            properties = new Properties();
        }

        return MapUtils.toProperties(MotechMapUtils.mergeMaps(properties, defaultProperties));
    }

    @Override
    public Map<String, Properties> getAllModuleProperties(String module, Map<String, Properties> allDefaultProperties) throws IOException {
        Map<String, Properties> allProperties = new HashMap<>();

        if (ConfigSource.UI.equals(configSource)) {
            List<String> filenameList = getFileNameList(modulePropertiesService.findByModule(module));
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
    public void addOrUpdateProperties(String module, String version, String bundle, String filename, Properties newProperties, Properties defaultProperties) throws IOException {
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
            File file = new File(String.format(STRING_FORMAT, getModuleConfigDir(module), filename));
            setUpDirsForFile(file);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                newProperties.store(fileOutputStream, null);
            }
        }
        ModulePropertiesRecord properties = new ModulePropertiesRecord(toPersist, module, version, bundle, filename, false);
        if (modulePropertiesService != null) {
            addOrUpdateModuleRecord(properties);
        }
    }

    @Override
    public void updatePropertiesAfterReinstallation(String module, String version, String bundle, String filename, Properties defaultProperties, Properties newProperties) throws IOException {
        if (!registersProperties(module, filename)) {
            addOrUpdateProperties(module, version, bundle, filename, newProperties, defaultProperties);
            return;
        }
        if (ConfigSource.UI.equals(configSource)) {
            Properties oldProperties = getModuleProperties(module, filename, defaultProperties);
            //Persist only non-default properties in database
            Properties toPersist = new Properties();
            Properties tempPropreties = (Properties) newProperties.clone();
            for (Map.Entry<Object, Object> entry : oldProperties.entrySet()) {
                if (newProperties.containsKey(entry.getKey())) {
                    tempPropreties.put(entry.getKey(), entry.getValue());
                }
            }

            for (Map.Entry<Object, Object> entry : tempPropreties.entrySet()) {
                if (!defaultProperties.containsKey(entry.getKey()) ||
                        (!defaultProperties.get(entry.getKey()).equals(tempPropreties.get(entry.getKey())))) {
                    toPersist.put(entry.getKey(), entry.getValue());
                }
            }

            ModulePropertiesRecord properties = new ModulePropertiesRecord(toPersist, module, version, bundle, filename, false);
            delete(module);
            addOrUpdateModuleRecord(properties);
        } else if (ConfigSource.FILE.equals(configSource)) {
            Properties currentProperties = getModuleProperties(module, filename, defaultProperties);
            Properties toStore = MotechMapUtils.asProperties(MotechMapUtils.mergeMaps(currentProperties, newProperties));

            File file = new File(String.format(STRING_FORMAT, getModuleConfigDir(module), filename));
            setUpDirsForFile(file);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                toStore.store(fileOutputStream, null);
            }
        }
    }

    public void removeProperties(String module, String filename) {
        if (ConfigSource.UI.equals(configSource)) {
            deleteByBundle(module);
        } else if (ConfigSource.FILE.equals(configSource)) {
            File file = new File(String.format(STRING_FORMAT, getModuleConfigDir(module), filename));
            if (!file.delete()) {
                throw new MotechConfigurationException("Could not delete configuration file");
            }
        }
    }

    @Override
    public void processExistingConfigs(List<File> files) {
        if (modulePropertiesService == null) {
            logger.warn("Unable to retrieve module properties ");
            return;
        }

        List<ModulePropertiesRecord> records = new ArrayList<>();
        List<ModulePropertiesRecord> dbRecords = modulePropertiesService.retrieveAll();

        for (File file : files) {
            if (isPlatformCoreConfigFile(file)) {
                savePlatformSettings(loadConfig());
                continue;
            }

            final ModulePropertiesRecord record = ModulePropertiesRecord.buildFrom(file);
            if (record == null) {
                continue;
            }
            ModulePropertiesRecord dbRecord = (ModulePropertiesRecord) CollectionUtils.find(dbRecords, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return record.sameAs(object);
                }
            });
            if (dbRecord != null) {
                dbRecords.remove(dbRecord);
            }
            records.add(record);
        }

        if (CollectionUtils.isNotEmpty(records)) {
            addOrUpdateModuleRecords(records);
        }

        if (CollectionUtils.isNotEmpty(dbRecords)) {
            removeModuleRecords(dbRecords);
        }
    }

    @Override
    public void addOrUpdate(File file) {
        if (isPlatformCoreConfigFile(file)) {
            savePlatformSettings(loadConfig());
            return;
        }

        addOrUpdateModuleRecord(ModulePropertiesRecord.buildFrom(file));
    }

    private SettingsRecord loadSettingsFromStream(org.springframework.core.io.Resource motechSettings) {
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

    @Override
    public void saveRawConfig(String module, String version, String bundle, String filename, InputStream rawData) throws IOException {
        if (ConfigSource.UI.equals(configSource)) {
            Properties p = new Properties();
            p.put("rawData", IOUtils.toString(rawData));
            ModulePropertiesRecord record = new ModulePropertiesRecord(p, module, version, bundle, filename, true);
            addOrUpdateModuleRecord(record);
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
            List<ModulePropertiesRecord> allRecords = modulePropertiesService.retrieveAll();
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
            List<ModulePropertiesRecord> records = modulePropertiesService.findByModule(module);
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
            ModulePropertiesRecord rec = getModulePropertiesRecord(module, filename);
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
        this.loadBootstrapConfig();
        if (ConfigSource.UI.equals(configSource)) {
            ModulePropertiesRecord rec = getModulePropertiesRecord(module, filename);
            return rec != null;
        } else {
            File file = new File(String.format(STRING_FORMAT, getModuleConfigDir(module), filename));
            return file.exists();
        }
    }

    @Override
    public void updateConfigLocation(String newConfigLocation) {
        try {
            coreConfigurationService.addConfigLocation(newConfigLocation);
        } catch (java.nio.file.FileSystemException e) {
            throw new MotechConfigurationException("Cannot add and/or update file monitoring location", e);
        }
    }

    @Override
    public void delete(String module) {
        List<ModulePropertiesRecord> records = modulePropertiesService.findByModule(module);
        modulePropertiesService.delete(records.get(0));
    }

    @Override
    public void deleteByBundle(String module) {
        List<ModulePropertiesRecord> records = modulePropertiesService.findByBundle(module);
        modulePropertiesService.delete(records.get(0));
    }

    @Override
    public boolean rawConfigExists(String module, String filename) {
        if (ConfigSource.UI.equals(configSource)) {
            ModulePropertiesRecord rec = getModulePropertiesRecord(module, filename);
            return (rec != null) && rec.isRaw();
        } else if (configSource != null && ConfigSource.FILE.equals(configSource)) {
            File file = new File(String.format("%s/raw/%s", getModuleConfigDir(module), filename));
            return file.exists();
        }
        return false;
    }

    private String getConfigDir() {
        if (coreConfigurationService == null) {
            return System.getProperty("user.home") + "/config";
        }
        return coreConfigurationService.getConfigLocation().getLocation();
    }

    private String getModuleConfigDir(String module) {
        return String.format("%s/%s/", getConfigDir(), module);
    }

    private static void setUpDirsForFile(File file) {
        file.getParentFile().mkdirs();
    }

    @Override
    public SettingsRecord loadDefaultConfig() {
        SettingsRecord settingsRecord = null;
        org.springframework.core.io.Resource defaultSettings = resourceLoader.getResource("classpath:motech-settings.properties");
        if (defaultSettings != null) {
            settingsRecord = loadSettingsFromStream(defaultSettings);
        }

        return settingsRecord;
    }

    @Override
    public SettingsRecord loadConfig() {
        return configLoader.loadMotechSettings();
    }

    @Override
    public boolean requiresConfigurationFiles() {
        try {
            if (getConfigSource() == null) {
                configSource = loadBootstrapConfig().getConfigSource();
            }
            if (!configSource.isFile()) {
                return false;
            }
            ConfigLocation configLocation = coreConfigurationService.getConfigLocation();
            return !configLocation.hasPlatformConfigurationFile();
        } catch (MotechConfigurationException ex) {
            logger.error(ex.getMessage(), ex);
            return true;
        }
    }

    void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @javax.annotation.Resource(name = "defaultSettings")
    public void setDefaultConfig(Properties defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    @javax.annotation.Resource(name = "defaultAnnotations")
    public void setConfigAnnotation(Properties configAnnotation) {
        this.configAnnotation = configAnnotation;
    }

    List<String> getFileNameList(List<ModulePropertiesRecord> records) {
        if (records.isEmpty()) {
            return null;
        }

        List<String> foundFiles = new ArrayList<>();
        for (ModulePropertiesRecord rec : records) {
            foundFiles.add(rec.getFilename());
        }
        return foundFiles;
    }

    @Override
    public void addOrUpdateModuleRecord(ModulePropertiesRecord record) {
        ModulePropertiesRecord rec = getModulePropertiesRecord(record.getModule(), record.getFilename());
        if (rec == null) {
            modulePropertiesService.create(record);
        } else {
            rec.setProperties(record.getProperties());
            modulePropertiesService.update(rec);
        }
    }

    @Override
    public void addOrUpdateModuleRecords(List<ModulePropertiesRecord> records) {
        for (ModulePropertiesRecord rec : records) {
            addOrUpdateModuleRecord(rec);
        }
    }

    @Override
    public void removeModuleRecords(List<ModulePropertiesRecord> records) {
        for (ModulePropertiesRecord rec : records) {
            modulePropertiesService.delete(rec);
        }
    }

    public SettingsRecord getSettings() {
        SettingsRecord settingRecord = settingService.retrieve("id", 1);
        return (settingRecord == null ? new SettingsRecord() :
                settingRecord);
    }

    public void addOrUpdateSettings(SettingsRecord settingsRecord) {
        SettingsRecord record = settingService.retrieve("id", 1);
        if (record == null) {
            settingService.create(settingsRecord);
        } else {
            record.setConfigFileChecksum(settingsRecord.getConfigFileChecksum());
            record.setFilePath(settingsRecord.getFilePath());
            record.setPlatformInitialized(settingsRecord.isPlatformInitialized());
            record.setPlatformSettings(settingsRecord.getPlatformSettings());
            settingService.update(record);
        }
    }

    ModulePropertiesRecord getModulePropertiesRecord(String module, String filename) {
        List<ModulePropertiesRecord>  records = (modulePropertiesService == null) ? null :
                modulePropertiesService.findByModuleAndFileName(module, filename);
        if (records != null) {
            for (ModulePropertiesRecord rec : records) {
                if (rec.getFilename().equals(filename)) {
                    return rec;
                }
            }
        }
        return null;
    }
}
