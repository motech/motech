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
import org.motechproject.config.repository.AllModuleProperties;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.repository.AllSettings;
import org.motechproject.server.config.service.ConfigLoader;
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

    private static Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);

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

    @Autowired
    private ConfigLoader configLoader;

    private ConfigSource configSource;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final String STRING_FORMAT = "%s/%s";

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

        configSource = bootstrapConfig.getConfigSource();

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
        if (allSettings == null) {
            return null;
        }
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
        Properties properties = allModuleProperties.asProperties(module, filename);
        return MapUtils.toProperties(MotechMapUtils.mergeMaps(properties, defaultProperties));
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
        }
        ModulePropertiesRecord properties = new ModulePropertiesRecord(toPersist, module, version, bundle, filename, false);
        allModuleProperties.addOrUpdate(properties);
    }

    @Override
    public void updatePropertiesAfterReinstallation(String module, String version, String bundle, String filename, Properties defaultProperties, Properties newProperties) throws IOException {
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
            allModuleProperties.addOrUpdate(properties);
        } else if (ConfigSource.FILE.equals(configSource)) {
            File file = new File(String.format(STRING_FORMAT, getModuleConfigDir(module), filename));
            setUpDirsForFile(file);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                newProperties.store(fileOutputStream, null);
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
        List<ModulePropertiesRecord> records = new ArrayList<>();
        List<ModulePropertiesRecord> dbRecords = allModuleProperties.getAll();

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
                    return record.sameAs((ModulePropertiesRecord) object);
                }
            });
            if (dbRecord != null) {
                dbRecords.remove(dbRecord);
                record.setId(dbRecord.getId());
                record.setRevision(dbRecord.getRevision());
            }
            records.add(record);
        }

        if (CollectionUtils.isNotEmpty(records)) {
            allModuleProperties.bulkAddOrUpdate(records);
        }
        if (CollectionUtils.isNotEmpty(dbRecords)) {
            allModuleProperties.bulkDelete(dbRecords);
        }
    }

    @Override
    public void addOrUpdate(File file) {
        if (isPlatformCoreConfigFile(file)) {
            savePlatformSettings(loadConfig());
            return;
        }

        allModuleProperties.addOrUpdate(ModulePropertiesRecord.buildFrom(file));
    }

    private SettingsRecord loadSettingsFromStream(org.springframework.core.io.Resource motechSettings) {
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

    @Override
    public void saveRawConfig(String module, String version, String bundle, String filename, InputStream rawData) throws IOException {
        if (ConfigSource.UI.equals(configSource)) {
            Properties p = new Properties();
            p.put("rawData", IOUtils.toString(rawData));
            ModulePropertiesRecord record = new ModulePropertiesRecord(p, module, version, bundle, filename, true);
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
        this.loadBootstrapConfig();
        if (ConfigSource.UI.equals(configSource)) {
            ModulePropertiesRecord rec = allModuleProperties.byModuleAndFileName(module, filename);
            return rec == null ? false : true;
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
        List<ModulePropertiesRecord> records = allModuleProperties.byModuleName(module);
        allModuleProperties.remove(records.get(0));
    }

    @Override
    public void deleteByBundle(String module) {
        List<ModulePropertiesRecord> records = allModuleProperties.byBundle(module);
        allModuleProperties.remove(records.get(0));
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

}
