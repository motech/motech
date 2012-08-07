package org.motechproject.admin.service.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.MotechException;
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
            Properties couchDbProperties = motechSettings.getCouchDBProperties();
            Settings couchSettings = new Settings("couchdb", ParamParser.parseProperties(couchDbProperties));
            ParamParser.convertNames(couchSettings.getSettings());
            settingsList.add(couchSettings);

            Properties activemqProperties = motechSettings.getActivemqProperties();
            Settings activemqSettings = new Settings("activemq", ParamParser.parseProperties(activemqProperties));
            settingsList.add(activemqSettings);

            Properties quartzProperties = motechSettings.getQuartzProperties();
            Settings quartzSettings = new Settings("quartz", ParamParser.parseProperties(quartzProperties));
            settingsList.add(quartzSettings);

            List<SettingsOption> miscOptions = new ArrayList<>();

            SettingsOption languageOption = ParamParser.parseParam(MotechSettings.LANGUAGE, motechSettings.getLanguage());
            miscOptions.add(languageOption);
            SettingsOption msgOption = ParamParser.parseParam(MotechSettings.STATUS_MSG_TIMEOUT, motechSettings.getStatusMsgTimeout());
            miscOptions.add(msgOption);

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
            LOG.error("Error while saving bundle settings", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void savePlatformSettings(Settings settings) {
        for (SettingsOption option : settings.getSettings()) {
            platformSettingsService.setPlatformSetting(option.getKey(), String.valueOf(option.getValue()));
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
        if (configFile == null) {
            throw new IllegalArgumentException("Config file cannot be null");
        }

        InputStream is = null;
        try {
            is = configFile.getInputStream();
            Properties settings = new Properties();
            settings.load(is);

            platformSettingsService.savePlatformSettings(settings);
        } catch (IOException e) {
            LOG.error("Unable to save config file", e);
            throw new MotechException("Error saving config file", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public void addSettingsPath(String path) {
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
