package org.motechproject.admin.service.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.settings.ParamParser;
import org.motechproject.admin.settings.Settings;
import org.motechproject.admin.settings.SettingsOption;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
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

@Service
public class SettingsServiceImpl implements SettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsServiceImpl.class);

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    private BundleContext bundleContext;

    @Override
    public List<Settings> getSettings() {
        MotechSettings motechSettings = platformSettingsService.getPlatformSettings();
        List<Settings> settingsList = new ArrayList<>();

        if (motechSettings != null) {
            Properties activemqProperties = motechSettings.getActivemqProperties();
            Settings activemqSettings = new Settings("activemq", ParamParser.parseProperties(activemqProperties));
            settingsList.add(activemqSettings);

            Properties quartzProperties = motechSettings.getQuartzProperties();
            Settings quartzSettings = new Settings("quartz", ParamParser.parseProperties(quartzProperties));
            settingsList.add(quartzSettings);

            Properties metricsProperties = motechSettings.getMetricsProperties();
            Settings metricsSettings = new Settings("metrics", ParamParser.parseProperties(metricsProperties));
            settingsList.add(metricsSettings);

            Properties schedulerProperties = motechSettings.getSchedulerProperties();
            Settings schedulerSettings = new Settings("scheduler", ParamParser.parseProperties(schedulerProperties));
            settingsList.add(schedulerSettings);

            List<SettingsOption> miscOptions = new ArrayList<>();

            SettingsOption languageOption = ParamParser.parseParam(MotechSettings.LANGUAGE, motechSettings.getLanguage());
            miscOptions.add(languageOption);
            SettingsOption msgOption = ParamParser.parseParam(MotechSettings.STATUS_MSG_TIMEOUT, motechSettings.getStatusMsgTimeout());
            miscOptions.add(msgOption);
            SettingsOption serverUrlOption = ParamParser.parseParam(MotechSettings.SERVER_URL, motechSettings.getServerUrl());
            miscOptions.add(serverUrlOption);

            Settings miscSettings = new Settings("other", miscOptions);
            settingsList.add(miscSettings);
        }
        return settingsList;
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
    public void savePlatformSettings(Settings settings) {
        if ("activemq".equals(settings.getSection())) {
            for (SettingsOption option : settings.getSettings()) {
                platformSettingsService.setActiveMqSetting(option.getKey(), String.valueOf(option.getValue()));
            }
        } else {
            for (SettingsOption option : settings.getSettings()) {
                platformSettingsService.setPlatformSetting(option.getKey(), String.valueOf(option.getValue()));
            }
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
        platformSettingsService.savePlatformSettings(settings);
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
    public void saveActiveMqFile(MultipartFile activemqFile) {
        Properties activemqSettings = loadMultipartFileIntoProperties(activemqFile);
        platformSettingsService.saveActiveMqSettings(activemqSettings);
    }

    @Override
    public void addSettingsPath(String path) throws IOException {
        platformSettingsService.addConfigLocation(path, true);
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
        return bundle.getSymbolicName();
    }
}
