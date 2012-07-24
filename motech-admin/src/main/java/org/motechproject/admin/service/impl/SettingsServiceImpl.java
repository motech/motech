package org.motechproject.admin.service.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.MotechException;
import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.settings.BundleSettings;
import org.motechproject.admin.settings.SettingsOption;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    private BundleContext bundleContext;

    @Override
    public List<SettingsOption> getSettings() {
        MotechSettings motechSettings = platformSettingsService.getPlatformSettings();
        Properties activemqProperties = motechSettings.getActivemqProperties();
        Properties quartzProperties = motechSettings.getQuartzProperties();
        Properties couchDbProperties = motechSettings.getCouchDBProperties();

        List<SettingsOption> settingsList = new ArrayList<>();
        settingsList.addAll(parseProperties(activemqProperties));
        settingsList.addAll(parseProperties(quartzProperties));
        settingsList.addAll(parseProperties(couchDbProperties));
        settingsList.add(parseParam(MotechSettings.LANGUAGE, motechSettings.getLanguage()));

        return settingsList;
    }

    @Override
    public List<BundleSettings> getBundleSettings(long bundleId) throws IOException {
        List<BundleSettings> bundleSettings = new ArrayList<>();
        String symbolicName = getSymbolicName(bundleId);

        for (Map.Entry<String, Properties> entry : platformSettingsService.getAllProperties(symbolicName).entrySet()) {
            List<SettingsOption> settingsList = new ArrayList<>();
            String filename = entry.getKey();
            Properties props = entry.getValue();
            for (Map.Entry<Object, Object> propEntry : props.entrySet()) {
                SettingsOption option = constructSettingsOption(propEntry);
                settingsList.add(option);
            }
            bundleSettings.add(new BundleSettings(filename, settingsList));
        }

        return bundleSettings;
    }

    @Override
    public void saveBundleSettings(List<SettingsOption> options, long bundleId) throws IOException {
        String symbolicName = getSymbolicName(bundleId);
        Map<String, Properties> propMap = new HashMap<>();
        for (SettingsOption option : options) {
            String filename = findFilenameForProperty(option.getKey(), symbolicName);
            if (!propMap.containsKey(filename)) {
                propMap.put(filename, new Properties());
            }
            propMap.get(filename).put(option.getKey(), option.getValue());
        }

        for (Map.Entry<String, Properties> entry : propMap.entrySet()) {
            platformSettingsService.saveBundleProperties(symbolicName, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void savePlatformSettings(List<SettingsOption> settingsOptions) {
        for (SettingsOption option : settingsOptions) {
            platformSettingsService.setPlatformSetting(option.getKey(), String.valueOf(option.getValue()));
        }
    }

    @Override
    public void saveSetting(SettingsOption option) {
        platformSettingsService.setPlatformSetting(option.getKey(), String.valueOf(option.getValue()));
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
            throw new MotechException("Error reading config file", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public void addSettingsPath(String path) {
        try {
            platformSettingsService.addConfigLocation(path, true);
        } catch (IOException e) {
            throw new MotechException("Error adding config location", e);
        }
    }

    private String getSymbolicName(long bundleId) {
        Bundle bundle = bundleContext.getBundle(bundleId);
        return bundle.getSymbolicName();
    }

    private static SettingsOption constructSettingsOption(Map.Entry<Object, Object> entry) {
        SettingsOption settingsOption = new SettingsOption();

        settingsOption.setValue(entry.getValue());
        settingsOption.setKey(String.valueOf(entry.getKey()));
        settingsOption.setType(entry.getValue().getClass().getSimpleName());

        return settingsOption;
    }

    private static List<SettingsOption> parseProperties(Properties props) {
        List<SettingsOption> settingsList = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            SettingsOption option = constructSettingsOption(entry);
            settingsList.add(option);
        }
        return settingsList;
    }

    private static SettingsOption parseParam(String key, String value) {
        SettingsOption settingsOption = new SettingsOption();

        settingsOption.setValue(value);
        settingsOption.setKey(key);
        settingsOption.setType(String.class.getSimpleName());

        return settingsOption;
    }

    private String findFilenameForProperty(String key, String symbolicName) throws IOException {
        String filename = null;
        Map<String, Properties> map = platformSettingsService.getAllProperties(symbolicName);
        for (Map.Entry<String, Properties> entry : map.entrySet()) {
            if (entry.getValue().containsKey(key)) {
                filename = entry.getKey();
                break;
            }
        }
        return filename;
    }
}
