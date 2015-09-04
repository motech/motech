package org.motechproject.server.config.domain;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.config.core.constants.ConfigurationConstants;

import java.io.IOException;
import java.security.DigestInputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Class for storing settings values.
 */
public class SettingsRecordDto implements MotechSettings {

    private boolean platformInitialized;
    private DateTime lastRun;
    private String filePath;
    private String configFileChecksum;
    private Map<String, String> platformSettings;

    @Override
    public String getLanguage() {
        return platformSettings.get(ConfigurationConstants.LANGUAGE);
    }

    @Override
    public String getStatusMsgTimeout() {
        return platformSettings.get(ConfigurationConstants.STATUS_MSG_TIMEOUT);
    }

    @Override
    public LoginMode getLoginMode() {
        return LoginMode.valueOf(platformSettings.get(ConfigurationConstants.LOGINMODE));
    }

    public String getLoginModeValue() {
        return platformSettings.get(ConfigurationConstants.LOGINMODE);
    }

    @Override
    public String getProviderName() {
        return platformSettings.get(ConfigurationConstants.PROVIDER_NAME);
    }

    @Override
    public String getProviderUrl() {
        return platformSettings.get(ConfigurationConstants.PROVIDER_URL);
    }

    @Override
    public String getServerUrl() {
        return new MotechURL(platformSettings.get(ConfigurationConstants.SERVER_URL)).toString();
    }

    @Override
    public String getJmxHost() {
        return platformSettings.get(ConfigurationConstants.JMX_HOST);
    }

    @Override
    public String getJmxBroker() {
        return platformSettings.get(ConfigurationConstants.JMX_BROKER);
    }

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

    @Override
    public String getUploadSize() {
        return platformSettings.get(ConfigurationConstants.UPLOAD_SIZE);
    }

    @Override
    public void setLanguage(final String language) {
        savePlatformSetting(ConfigurationConstants.LANGUAGE, language);
    }

    @Override
    public void setLoginModeValue(String loginMode) {
        savePlatformSetting(ConfigurationConstants.LOGINMODE, loginMode);
    }

    @Override
    public void setProviderName(String providerName) {
        savePlatformSetting(ConfigurationConstants.PROVIDER_NAME, providerName);
    }

    @Override
    public void setProviderUrl(String providerUrl) {
        savePlatformSetting(ConfigurationConstants.PROVIDER_URL, providerUrl);
    }

    @Override
    public void setJmxHost(String jmxHost) {
        savePlatformSetting(ConfigurationConstants.JMX_HOST, jmxHost);
    }

    @Override
    public void setJmxBroker(String jmxBroker) {
        savePlatformSetting(ConfigurationConstants.JMX_BROKER, jmxBroker);
    }

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

    @Override
    public void setServerUrl(String serverUrl) {
        savePlatformSetting(ConfigurationConstants.SERVER_URL, serverUrl);
    }

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

    @Override
    public void updateFromProperties(final Properties props) {
        if (props != null) {
            for (Object key : props.keySet()) {
                savePlatformSetting(key.toString(), props.get(key).toString());
            }
        }
    }

    @Override
    public void savePlatformSetting(String key, String value) {
        // cant store nulls in persistent maps
        platformSettings.put(key, value == null ? "" : value);
    }


    @Override
    public synchronized void load(DigestInputStream dis) throws IOException {
        Properties props = new Properties();
        props.load(dis);
        for (Object key : props.keySet()) {
            savePlatformSetting(key.toString(), props.get(key).toString());
        }
    }

    @Override
    public Properties asProperties() {
        return MapUtils.toProperties(platformSettings);
    }

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

    public void setPlatformSettings(Map<String, String> platformSettings) {
        this.platformSettings = platformSettings;
    }

    public Map<String, String> getPlatformSettings() {
        return this.platformSettings;
    }

    @Override
    public boolean getEmailRequired() {
        return Boolean.parseBoolean(platformSettings.get(ConfigurationConstants.EMAIL_REQUIRED));
    }

    @Override
    public void setEmailRequired(String emailRequired) {
        savePlatformSetting(ConfigurationConstants.EMAIL_REQUIRED,
                emailRequired);
    }

    @Override
    public Integer getSessionTimeout() {
        return getInteger(ConfigurationConstants.SESSION_TIMEOUT);
    }

    @Override
    public void setSessionTimeout(Integer sessionTimeout) {
        savePlatformSetting(ConfigurationConstants.SESSION_TIMEOUT, intToStr(sessionTimeout));
    }

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

    @Override
    public int getFailureLoginLimit() {
        String value = platformSettings.get(ConfigurationConstants.FAILURE_LOGIN_LIMIT);
        return value == null ? 0 : Integer.parseInt(value);
    }

    @Override
    public void setFailureLoginLimit(int limit) {
        savePlatformSetting(ConfigurationConstants.FAILURE_LOGIN_LIMIT,
                String.valueOf(limit));
    }

    private Integer getInteger(String key) {
        String value = platformSettings.get(key);
        return StringUtils.isBlank(value) ? null : Integer.valueOf(value);
    }

    private String intToStr(Integer integer) {
        return integer == null ? null : String.valueOf(integer);
    }
}
