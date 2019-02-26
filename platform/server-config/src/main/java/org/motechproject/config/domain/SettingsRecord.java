package org.motechproject.config.domain;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.config.wrapper.MotechURL;

import java.io.IOException;
import java.security.DigestInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Class for storing settings values.
 */
@Entity(recordHistory = true)
@Access(value = SecurityMode.PERMISSIONS, members = {"manageSettings"})
public class SettingsRecord implements MotechSettings {

    @Field
    @UIDisplayable
    private boolean platformInitialized;

    @Field
    @UIDisplayable
    private DateTime lastRun;

    @Field
    @UIDisplayable
    private String filePath;

    @Field
    private String configFileChecksum;

    @Field
    @UIDisplayable
    private Map<String, String> platformSettings;

    public SettingsRecord() {
        platformSettings = new HashMap<>();
    }

    @Ignore
    @Override
    public String getLanguage() {
        return platformSettings.get(ConfigurationConstants.LANGUAGE);
    }

    @Ignore
    @Override
    public String getStatusMsgTimeout() {
        return platformSettings.get(ConfigurationConstants.STATUS_MSG_TIMEOUT);
    }

    @Ignore
    @Override
    public LoginMode getLoginMode() {
        return LoginMode.valueOf(platformSettings.get(ConfigurationConstants.LOGINMODE));
    }

    @Ignore
    public String getLoginModeValue() {
        return platformSettings.get(ConfigurationConstants.LOGINMODE);
    }

    @Ignore
    @Override
    public String getProviderName() {
        return platformSettings.get(ConfigurationConstants.PROVIDER_NAME);
    }

    @Ignore
    @Override
    public String getProviderUrl() {
        return platformSettings.get(ConfigurationConstants.PROVIDER_URL);
    }

    @Ignore
    @Override
    public String getServerUrl() {
        return new MotechURL(platformSettings.get(ConfigurationConstants.SERVER_URL)).toString();
    }

    @Ignore
    @Override
    public String getJmxHost() {
        return platformSettings.get(ConfigurationConstants.JMX_HOST);
    }

    @Ignore
    @Override
    public String getJmxBroker() {
        return platformSettings.get(ConfigurationConstants.JMX_BROKER);
    }

    @Ignore
    @Override
    public String getJmxUsername() {
        return platformSettings.get(ConfigurationConstants.JMX_USERNAME);
    }

    @Ignore
    @Override
    public String getJmxPassword() {
        return platformSettings.get(ConfigurationConstants.JMX_PASSWORD);
    }

    @Ignore
    @Override
    public String getServerHost() {
        return new MotechURL(platformSettings.get(ConfigurationConstants.SERVER_URL)).getHost();
    }

    @Override
    public boolean isPlatformInitialized() {
        return platformInitialized;
    }

    @Override
    public void setPlatformInitialized(boolean platformInitialized) {
        this.platformInitialized = platformInitialized;
    }

    @Ignore
    @Override
    public String getUploadSize() {
        return platformSettings.get(ConfigurationConstants.UPLOAD_SIZE);
    }

    @Ignore
    @Override
    public void setLanguage(final String language) {
        savePlatformSetting(ConfigurationConstants.LANGUAGE, language);
    }

    @Ignore
    @Override
    public void setLoginModeValue(String loginMode) {
        savePlatformSetting(ConfigurationConstants.LOGINMODE, loginMode);
    }

    @Ignore
    @Override
    public void setProviderName(String providerName) {
        savePlatformSetting(ConfigurationConstants.PROVIDER_NAME, providerName);
    }

    @Ignore
    @Override
    public void setProviderUrl(String providerUrl) {
        savePlatformSetting(ConfigurationConstants.PROVIDER_URL, providerUrl);
    }

    @Ignore
    @Override
    public void setJmxHost(String jmxHost) {
        savePlatformSetting(ConfigurationConstants.JMX_HOST, jmxHost);
    }

    @Ignore
    @Override
    public void setJmxBroker(String jmxBroker) {
        savePlatformSetting(ConfigurationConstants.JMX_BROKER, jmxBroker);
    }

    @Ignore
    @Override
    public void setJmxUsername(String jmxUsername) {
        savePlatformSetting(ConfigurationConstants.JMX_USERNAME, jmxUsername);
    }

    @Ignore
    @Override
    public void setJmxPassword(String jmxPassword) {
        savePlatformSetting(ConfigurationConstants.JMX_PASSWORD, jmxPassword);
    }
    @Ignore
    @Override
    public void setStatusMsgTimeout(final String statusMsgTimeout) {
        savePlatformSetting(ConfigurationConstants.STATUS_MSG_TIMEOUT, statusMsgTimeout);
    }

    @Override
    public DateTime getLastRun() {
        return lastRun;
    }

    @Override
    public void setLastRun(final DateTime lastRun) {
        this.lastRun = lastRun;
    }

    @Ignore
    @Override
    public void setServerUrl(String serverUrl) {
        savePlatformSetting(ConfigurationConstants.SERVER_URL, serverUrl);
    }

    @Ignore
    @Override
    public void setUploadSize(String uploadSize) {
        savePlatformSetting(ConfigurationConstants.UPLOAD_SIZE, uploadSize);
    }

    @Override
    public String getConfigFileChecksum() {
        return configFileChecksum;
    }

    @Override
    public void setConfigFileChecksum(final String configFileChecksum) {
        this.configFileChecksum = configFileChecksum;
    }

    @Ignore
    @Override
    public void updateFromProperties(final Properties props) {
        if (props != null) {
            for (Object key : props.keySet()) {
                savePlatformSetting(key.toString(), props.get(key).toString());
            }
        }
    }

    @Ignore
    @Override
    public void savePlatformSetting(String key, String value) {
        // cant store nulls in persistent maps
        platformSettings.put(key, value == null ? "" : value);
    }


    @Ignore
    @Override
    public synchronized void load(DigestInputStream dis) throws IOException {
        Properties props = new Properties();
        props.load(dis);
        for (Object key : props.keySet()) {
            savePlatformSetting(key.toString(), props.get(key).toString());
        }
    }

    @Ignore
    @Override
    public Properties asProperties() {
        return MapUtils.toProperties(platformSettings);
    }

    @Ignore
    @Override
    public void updateSettings(final String configFileChecksum, String filePath, Properties platformSettings) {
        setConfigFileChecksum(configFileChecksum);
        this.filePath = filePath;
        updateFromProperties(platformSettings);
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Merges given default configuration into existing platform settings. Keys that already exists won't be overwritten.
     *
     * @param defaultConfig  the default configuration to be merged.
     */
    @Ignore
    public void mergeWithDefaults(Properties defaultConfig) {
        if (defaultConfig != null) {
            for (Object key : defaultConfig.keySet()) {
                if (!platformSettings.containsKey(key.toString())) {
                    savePlatformSetting(key.toString(), defaultConfig.get(key).toString());
                }
            }
        }
    }

    /**
     * Removes settings specified in defaultConfig.
     *
     * @param defaultConfig
     */
    @Ignore
    public void removeDefaults(Properties defaultConfig) {
        for (Map.Entry<Object, Object> entry : defaultConfig.entrySet()) {
            String key = (String) entry.getKey();
            Object defaultValue = entry.getValue();

            Object currentVal = platformSettings.get(key);

            if (currentVal != null && Objects.equals(currentVal, defaultValue)) {
                platformSettings.remove(key);
            }
        }
    }

    public void setPlatformSettings(Map<String, String> platformSettings) {
        this.platformSettings = platformSettings;
    }

    public Map<String, String> getPlatformSettings() {
        return this.platformSettings;
    }

    @Ignore
    @Override
    public boolean getEmailRequired() {
        return Boolean.parseBoolean(platformSettings.get(ConfigurationConstants.EMAIL_REQUIRED));
    }

    @Override
    public void setEmailRequired(String emailRequired) {
        savePlatformSetting(ConfigurationConstants.EMAIL_REQUIRED,
                emailRequired);
    }


    @Ignore
    @Override
    public Integer getSessionTimeout() {
        return getInteger(ConfigurationConstants.SESSION_TIMEOUT);
    }

    @Override
    public void setSessionTimeout(Integer sessionTimeout) {
        savePlatformSetting(ConfigurationConstants.SESSION_TIMEOUT, intToStr(sessionTimeout));
    }

    @Ignore
    @Override
    public String getPasswordValidator() {
        String validator = platformSettings.get(ConfigurationConstants.PASSWORD_VALIDATOR);
        return validator == null ? "" : validator;
    }

    @Override
    public void setPasswordValidator(String validator) {
        savePlatformSetting(ConfigurationConstants.PASSWORD_VALIDATOR, validator);
    }

    @Override
    public Integer getMinPasswordLength() {
        return getInteger(ConfigurationConstants.MIN_PASSWORD_LENGTH);
    }

    @Override
    public void setMinPasswordLength(Integer minPasswordLength) {
        platformSettings.put(ConfigurationConstants.MIN_PASSWORD_LENGTH, intToStr(minPasswordLength));
    }

    @Ignore
    @Override
    public Integer getFailureLoginLimit() {
        return getInteger(ConfigurationConstants.FAILURE_LOGIN_LIMIT);
    }

    @Override
    public void setFailureLoginLimit(int limit) {
        savePlatformSetting(ConfigurationConstants.FAILURE_LOGIN_LIMIT,
                String.valueOf(limit));
    }

    @Ignore
    @Override
    public Integer getNumberOfDaysToChangePassword() {
        return getInteger(ConfigurationConstants.PASSWORD_RESET_DAYS);
    }

    @Override
    public void setNumberOfDaysToChangePassword(Integer days) {
        savePlatformSetting(ConfigurationConstants.PASSWORD_RESET_DAYS, intToStr(days));
    }

    @Ignore
    @Override
    public boolean isPasswordResetReminderEnabled() {
        return Boolean.parseBoolean(platformSettings.get(ConfigurationConstants.PASSWORD_REMINDER));
    }

    @Override
    public void setPasswordResetReminder(String remind) {
        savePlatformSetting(ConfigurationConstants.PASSWORD_REMINDER,
                remind);
    }

    @Ignore
    @Override
    public Integer getNumberOfDaysForReminder() {
        return getInteger(ConfigurationConstants.PASSWORD_REMINDER_DAYS);
    }

    @Override
    public void setNumberOfDaysForReminder(Integer days) {
        savePlatformSetting(ConfigurationConstants.PASSWORD_REMINDER_DAYS, intToStr(days));
    }

    private Integer getInteger(String key) {
        String value = platformSettings.get(key);
        return StringUtils.isBlank(value) ? null : Integer.valueOf(value);
    }

    private String intToStr(Integer integer) {
        return integer == null ? null : String.valueOf(integer);
    }
}
