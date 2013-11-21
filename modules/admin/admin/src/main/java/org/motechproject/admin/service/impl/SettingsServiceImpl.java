package org.motechproject.admin.service.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.admin.domain.AdminSettings;
import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.settings.ParamParser;
import org.motechproject.admin.settings.Settings;
import org.motechproject.admin.settings.SettingsOption;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Implementation of {@link SettingsService} interface for settings management.
 */
@Service
public class SettingsServiceImpl implements SettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsServiceImpl.class);

    @Autowired
    private PlatformSettingsService platformSettingsService;

    private CoreConfigurationService coreConfigurationService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BundleContext bundleContext;

    @Override
    public AdminSettings getSettings() {
        MotechSettings motechSettings = configurationService.getPlatformSettings();
        List<Settings> settingsList = new ArrayList<>();
        AdminSettings adminSettings = new AdminSettings(settingsList, false);

        if (motechSettings != null) {
            Properties activemqProperties = motechSettings.getActivemqProperties();
            Settings activemqSettings = new Settings("activemq", ParamParser.parseProperties(activemqProperties));
            settingsList.add(activemqSettings);

            List<SettingsOption> miscOptions = new ArrayList<>();

            SettingsOption languageOption = ParamParser.parseParam(ConfigurationConstants.LANGUAGE, motechSettings.getLanguage());
            miscOptions.add(languageOption);
            SettingsOption msgOption = ParamParser.parseParam(ConfigurationConstants.STATUS_MSG_TIMEOUT, motechSettings.getStatusMsgTimeout());
            miscOptions.add(msgOption);
            SettingsOption serverUrlOption = ParamParser.parseParam(ConfigurationConstants.SERVER_URL, motechSettings.getServerUrl());
            miscOptions.add(serverUrlOption);
            SettingsOption uploadSizeOption = ParamParser.parseParam(ConfigurationConstants.UPLOAD_SIZE, motechSettings.getUploadSize());
            miscOptions.add(uploadSizeOption);

            Settings miscSettings = new Settings("other", miscOptions);
            settingsList.add(miscSettings);
            if (ConfigSource.FILE.equals(configurationService.getConfigSource())) {
                adminSettings = new AdminSettings(settingsList, true);
            } else {
                adminSettings = new AdminSettings(settingsList, false);
            }
        }
        return adminSettings;
    }

    @Override
    public List<Settings> getBundleSettings(long bundleId) throws IOException {
        List<Settings> bundleSettings = new ArrayList<>();
        String symbolicName = getSymbolicName(bundleId);

        Map<String, Properties> allDefaultProperties = getBundleDefaultProperties(bundleId);
        Map<String, Properties> allModuleEntries = configurationService.getAllModuleProperties(symbolicName, allDefaultProperties);

        for (Map.Entry<String, Properties> entry : allModuleEntries.entrySet()) {
            List<SettingsOption> settingsList = ParamParser.parseProperties(entry.getValue());
            bundleSettings.add(new Settings(entry.getKey(), settingsList));
        }

        return bundleSettings;
    }

    @Override
    public void saveBundleSettings(Settings settings, long bundleId) {
        String symbolicName = getSymbolicName(bundleId);
        Properties props = ParamParser.constructProperties(settings);

        try {
            configurationService.addOrUpdateProperties(symbolicName, settings.getSection(),
                    props, getBundleDefaultProperties(bundleId).get(settings.getSection()));
        } catch (Exception e) {
            throw new MotechException("Error while saving bundle settings", e);
        }
    }

    @Override
    public InputStream exportConfig(String fileName) throws IOException {
        return configurationService.createZipWithConfigFiles(ConfigurationConstants.SETTINGS_FILE_NAME, fileName);
    }

    @Override
    public void savePlatformSettings(Settings settings) {
        for (SettingsOption option : settings.getSettings()) {
            configurationService.setPlatformSetting(option.getKey(), String.valueOf(option.getValue()));
        }
    }

    @Override
    public void savePlatformSettings(List<Settings> settings) {
        for (Settings s : settings) {
            savePlatformSettings(s);
        }
    }

    @Override
    public void saveSettingsFile(MultipartFile configFile) {
        Properties settings = loadMultipartFileIntoProperties(configFile);
        configurationService.savePlatformSettings(settings);
    }

    private Properties loadMultipartFileIntoProperties(MultipartFile configFile) {
        if (configFile == null) {
            throw new IllegalArgumentException("Config file cannot be null");
        }

        InputStream is = null;
        Properties settings = new Properties();
        try {
            is = configFile.getInputStream();

            settings.load(is);

        } catch (IOException e) {
            LOG.error("Unable to save config file", e);
            throw new MotechException("Error saving config file", e);
        } finally {
            IOUtils.closeQuietly(is);
        }

        return settings;
    }

    @Override
    public void addSettingsPath(String newConfigLocation) throws IOException {
        configurationService.updateConfigLocation(newConfigLocation);
    }

    @Override
    public List<String> retrieveRegisteredBundleNames() {
        return configurationService.retrieveRegisteredBundleNames();
    }

    @Override
    public List<String> getRawFilenames(long bundleId) {
        return configurationService.listRawConfigNames(getSymbolicName(bundleId));
    }

    @Override
    public void saveRawFile(MultipartFile file, String filename, long bundleId) {
        InputStream is = null;
        String symbolicName = getSymbolicName(bundleId);
        try {
            is = file.getInputStream();
            configurationService.saveRawConfig(symbolicName, filename, is);
        } catch (IOException e) {
            LOG.error("Error reading uploaded file", e);
            throw new MotechException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private Map<String,Properties> getBundleDefaultProperties(long bundleId) throws IOException {
        Bundle bundle = bundleContext.getBundle(bundleId);
        //Find all property files in main bundle directory
        Enumeration<URL> enumeration = bundle.findEntries("", "*.properties",false);
        Map<String,Properties> allDefaultProperties = new LinkedHashMap<>();

        while (enumeration.hasMoreElements()) {
            InputStream is = null;
            URL url = enumeration.nextElement();
            try {
                is = url.openStream();
                Properties defaultBundleProperties = new Properties();
                defaultBundleProperties.load(is);
                if (!url.getFile().isEmpty()) {
                    //We want to store plain filename, without unnecessary slash prefix
                    allDefaultProperties.put(url.getFile().substring(1), defaultBundleProperties);
                }
            } catch (IOException e) {
                LOG.error("Error while reading or retrieving default properties", e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        return allDefaultProperties;
    }

    private String getSymbolicName(long bundleId) {
        Bundle bundle = bundleContext.getBundle(bundleId);
        String symbolicName = bundle.getSymbolicName();

        return symbolicName.endsWith("-bundle") ? symbolicName : symbolicName + "-bundle";
    }
}
