package org.motechproject.admin.internal.service.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.admin.internal.service.SettingsService;
import org.motechproject.admin.settings.AdminSettings;
import org.motechproject.admin.settings.ParamParser;
import org.motechproject.admin.settings.Settings;
import org.motechproject.admin.settings.SettingsOption;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.config.domain.MotechSettings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Implementation of {@link SettingsService} interface for settings management.
 */
@Service
public class SettingsServiceImpl implements SettingsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsServiceImpl.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public AdminSettings getSettings() {
        MotechSettings motechSettings = configurationService.getPlatformSettings();
        List<Settings> settingsList = new ArrayList<>();
        AdminSettings adminSettings = new AdminSettings(settingsList, false);

        if (motechSettings != null) {
            List<SettingsOption> generalOptions = new ArrayList<>();
            List<SettingsOption> securityOptions = new ArrayList<>();
            List<SettingsOption> jmxOptions = new ArrayList<>();

            SettingsOption languageOption = ParamParser.parseParam(ConfigurationConstants.LANGUAGE, motechSettings.getLanguage());
            generalOptions.add(languageOption);
            SettingsOption msgOption = ParamParser.parseParam(ConfigurationConstants.STATUS_MSG_TIMEOUT, motechSettings.getStatusMsgTimeout());
            generalOptions.add(msgOption);
            SettingsOption serverUrlOption = ParamParser.parseParam(ConfigurationConstants.SERVER_URL, motechSettings.getServerUrl());
            generalOptions.add(serverUrlOption);
            SettingsOption uploadSizeOption = ParamParser.parseParam(ConfigurationConstants.UPLOAD_SIZE, motechSettings.getUploadSize());
            generalOptions.add(uploadSizeOption);

            SettingsOption emailRequiredOption = ParamParser.parseParam(ConfigurationConstants.EMAIL_REQUIRED, motechSettings.getEmailRequired());
            securityOptions.add(emailRequiredOption);
            SettingsOption sessionTimeoutOption = ParamParser.parseParam(ConfigurationConstants.SESSION_TIMEOUT, motechSettings.getSessionTimeout());
            securityOptions.add(sessionTimeoutOption);
            SettingsOption failureLoginLimit = ParamParser.parseParam(ConfigurationConstants.FAILURE_LOGIN_LIMIT, motechSettings.getFailureLoginLimit());
            securityOptions.add(failureLoginLimit);
            SettingsOption minPasswordLengthOption = ParamParser.parseParam(ConfigurationConstants.MIN_PASSWORD_LENGTH, motechSettings.getMinPasswordLength());
            securityOptions.add(minPasswordLengthOption);
            SettingsOption passwordValidatorOption = ParamParser.parseParam(ConfigurationConstants.PASSWORD_VALIDATOR, motechSettings.getPasswordValidator());
            securityOptions.add(passwordValidatorOption);
            SettingsOption passwordResetOption = ParamParser.parseParam(ConfigurationConstants.PASSWORD_RESET_DAYS, motechSettings.getNumberOfDaysToChangePassword());
            securityOptions.add(passwordResetOption);
            SettingsOption passwordReminderOption = ParamParser.parseParam(ConfigurationConstants.PASSWORD_REMINDER, motechSettings.isPasswordResetReminderEnabled());
            securityOptions.add(passwordReminderOption);
            SettingsOption passwordRemindDaysOption = ParamParser.parseParam(ConfigurationConstants.PASSWORD_REMINDER_DAYS, motechSettings.getNumberOfDaysForReminder());
            securityOptions.add(passwordRemindDaysOption);

            SettingsOption jmxUrlOption = ParamParser.parseParam(ConfigurationConstants.JMX_HOST, motechSettings.getJmxHost());
            jmxOptions.add(jmxUrlOption);
            SettingsOption jmxBrokerOption = ParamParser.parseParam(ConfigurationConstants.JMX_BROKER, motechSettings.getJmxBroker());
            jmxOptions.add(jmxBrokerOption);

            Settings generalSettings = new Settings("general", generalOptions);
            Settings securitySettings = new Settings("security", securityOptions);
            Settings jmxSettings = new Settings("jmx", jmxOptions);

            settingsList.add(generalSettings);
            settingsList.add(securitySettings);
            settingsList.add(jmxSettings);

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

        Map<String, Properties> allDefaultProperties = getBundleDefaultProperties(bundleId);
        Map<String, Properties> allModuleEntries = configurationService.getAllBundleProperties(getBundleSymbolicName(bundleId),
                allDefaultProperties);

        for (Map.Entry<String, Properties> entry : allModuleEntries.entrySet()) {
            List<SettingsOption> settingsList = ParamParser.parseProperties(entry.getValue());
            bundleSettings.add(new Settings(entry.getKey(), settingsList));
        }

        return bundleSettings;
    }

    @Override
    public void saveBundleSettings(Settings settings, long bundleId) throws IOException {
        Properties props = ParamParser.constructProperties(settings);

        configurationService.addOrUpdateProperties(getBundleSymbolicName(bundleId), getVersion(bundleId), settings.getSection(),
                props, getBundleDefaultProperties(bundleId).get(settings.getSection()));

        Map<String, Object> params = new HashMap<>();
        params.put(ConfigurationConstants.BUNDLE_ID, bundleId);
        params.put(ConfigurationConstants.BUNDLE_SYMBOLIC_NAME, getBundleSymbolicName(bundleId));
        params.put(ConfigurationConstants.BUNDLE_SECTION, settings.getSection());

        MotechEvent bundleSettingsChangedEvent = new MotechEvent(ConfigurationConstants.BUNDLE_SETTINGS_CHANGED_EVENT_SUBJECT, params);
        eventRelay.sendEventMessage(bundleSettingsChangedEvent);
    }

    @Override
    public InputStream exportConfig(String fileName) throws IOException {
        return configurationService.createZipWithConfigFiles(ConfigurationConstants.SETTINGS_FILE_NAME, fileName);
    }

    @Override
    public void savePlatformSettings(Settings settings) {
        for (SettingsOption option : settings.getSettings()) {
            Object val = option.getValue();
            configurationService.setPlatformSetting(option.getKey(), val == null ? null : String.valueOf(val));
        }

        Map<String, Object> params = new HashMap<>();
        params.put(ConfigurationConstants.SETTINGS, settings);

        MotechEvent platformSettingsChangedEvent = new MotechEvent(ConfigurationConstants.BUNDLE_SETTINGS_CHANGED_EVENT_SUBJECT, params);
        eventRelay.sendEventMessage(platformSettingsChangedEvent);

    }

    @Override
    public void savePlatformSettings(List<Settings> settings) {
        for (Settings s : settings) {
            savePlatformSettings(s);
        }

        Map<String, Object> params = new HashMap<>();
        params.put(ConfigurationConstants.SETTINGS, settings);

        MotechEvent platformSettingsChangedEvent = new MotechEvent(ConfigurationConstants.PLATFORM_SETTINGS_CHANGED_EVENT_SUBJECT, params);
        eventRelay.sendEventMessage(platformSettingsChangedEvent);
    }

    @Override
    public void saveSettingsFile(MultipartFile configFile) {
        Properties settings = loadMultipartFileIntoProperties(configFile);
        configurationService.savePlatformSettings(settings);

        Map<String, Object> params = new HashMap<>();
        params.put(ConfigurationConstants.SETTINGS, settings);

        MotechEvent platformSettingsChangedEvent = new MotechEvent(ConfigurationConstants.PLATFORM_SETTINGS_CHANGED_EVENT_SUBJECT, params);
        eventRelay.sendEventMessage(platformSettingsChangedEvent);

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
        return configurationService.listRawConfigNames(getBundleSymbolicName(bundleId));
    }

    @Override
    public void saveRawFile(MultipartFile file, String filename, long bundleId) {
        InputStream is = null;

        try {
            is = file.getInputStream();
            configurationService.saveRawConfig(getBundleSymbolicName(bundleId), getVersion(bundleId), filename, is);
        } catch (IOException e) {
            throw new MotechException("Error reading uploaded file", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private Map<String, Properties> getBundleDefaultProperties(long bundleId) throws IOException {
        Bundle bundle = bundleContext.getBundle(bundleId);
        //Find all property files in main bundle directory
        Enumeration<URL> enumeration = bundle.findEntries("", "*.properties", false);
        Map<String, Properties> allDefaultProperties = new LinkedHashMap<>();

        if (enumeration != null) {
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
                    LOGGER.error("Error while reading or retrieving default properties", e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }
        return allDefaultProperties;
    }

    private String getVersion(long bundleId) {
        Bundle bundle = bundleContext.getBundle(bundleId);
        Version version = bundle.getVersion();

        return version != null ? version.toString() : "";
    }

    private String getBundleSymbolicName(long bundleId) {
        Bundle bundle = bundleContext.getBundle(bundleId);

        return bundle.getSymbolicName();
    }

}
