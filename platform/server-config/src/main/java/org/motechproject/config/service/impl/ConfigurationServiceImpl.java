package org.motechproject.config.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.bootstrap.BootstrapConfigManager;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.repository.AllSettings;
import org.motechproject.server.config.domain.MotechSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    @Autowired
    private ConfigFileMonitor configFileMonitor;

    @Autowired
    private BootstrapConfigManager bootstrapConfigManager;

    @Autowired
    private AllSettings allSettings;

    @Resource(name = "defaultSettings")
    private Properties defaultConfig;

    @Resource(name = "defaultAnnotations")
    private Properties configAnnotation;

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading bootstrap configuration.");
        }

        final BootstrapConfig bootstrapConfig = bootstrapConfigManager.loadBootstrapConfig();

        if (bootstrapConfig == null) {
            return null;
        }

        if (ConfigSource.FILE.equals(bootstrapConfig.getConfigSource())) {
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
    public void save(BootstrapConfig bootstrapConfig) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving bootstrap configuration.");
        }

        bootstrapConfigManager.saveBootstrapConfig(bootstrapConfig);

        if (logger.isDebugEnabled()) {
            logger.debug("Saved bootstrap configuration:" + bootstrapConfig);
        }
    }

    @Override
    @Caching(cacheable = { @Cacheable(value = SETTINGS_CACHE_NAME, key = "#root.methodName") })
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
}
