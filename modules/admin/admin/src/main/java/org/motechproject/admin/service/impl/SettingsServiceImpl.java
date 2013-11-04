package org.motechproject.admin.service.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.admin.domain.AdminSettings;
import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.settings.ParamParser;
import org.motechproject.admin.settings.Settings;
import org.motechproject.admin.settings.SettingsOption;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.domain.MotechSettings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    private ConfigurationService configurationService;

    @Autowired
    private PlatformSettingsService platformSettingsService;

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

            SettingsOption languageOption = ParamParser.parseParam(MotechSettings.LANGUAGE, motechSettings.getLanguage());
            miscOptions.add(languageOption);
            SettingsOption msgOption = ParamParser.parseParam(MotechSettings.STATUS_MSG_TIMEOUT, motechSettings.getStatusMsgTimeout());
            miscOptions.add(msgOption);
            SettingsOption serverUrlOption = ParamParser.parseParam(MotechSettings.SERVER_URL, motechSettings.getServerUrl());
            miscOptions.add(serverUrlOption);
            SettingsOption uploadSizeOption = ParamParser.parseParam(MotechSettings.UPLOAD_SIZE, motechSettings.getUploadSize());
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

        for (Map.Entry<String, Properties> entry : platformSettingsService.getAllProperties(symbolicName).entrySet()) {
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
            platformSettingsService.saveBundleProperties(symbolicName, settings.getSection(), props);
        } catch (IOException e) {
            throw new MotechException("Error while saving bundle settings", e);
        }
    }

    @Override
    public InputStream exportConfig(String fileName) throws IOException {
        return configurationService.createZipWithConfigFiles(MotechSettings.SETTINGS_FILE_NAME, fileName);
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
    public void addSettingsPath(String path) throws IOException {
        platformSettingsService.addConfigLocation(path);
    }

    @Override
    public List<String> retrieveRegisteredBundleNames() {
        return platformSettingsService.retrieveRegisteredBundleNames();
    }

    @Override
    public List<String> getRawFilenames(long bundleId) {
        return platformSettingsService.listRawConfigNames(getSymbolicName(bundleId));
    }

    @Override
    public void saveRawFile(MultipartFile file, String filename, long bundleId) {
        InputStream is = null;
        String symbolicName = getSymbolicName(bundleId);
        try {
            is = file.getInputStream();
            platformSettingsService.saveRawConfig(symbolicName, filename, is);
        } catch (IOException e) {
            LOG.error("Error reading uploaded file", e);
            throw new MotechException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private String getSymbolicName(long bundleId) {
        Bundle bundle = bundleContext.getBundle(bundleId);
        String symbolicName = bundle.getSymbolicName();

        return symbolicName.endsWith("-bundle") ? symbolicName : symbolicName + "-bundle";
    }
}
