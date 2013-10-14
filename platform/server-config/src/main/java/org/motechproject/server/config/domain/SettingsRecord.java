package org.motechproject.server.config.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Class for storing settings values
 */

@TypeDiscriminator("doc.type === 'SettingsRecord'")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "couchDbProperties" })
public class SettingsRecord extends MotechBaseDataObject implements MotechSettings {

    private String language;
    private String statusMsgTimeout;
    private String loginMode;
    private String providerName;
    private String providerUrl;
    private String serverUrl;
    private String uploadSize;

    private boolean cluster;
    private boolean platformInitialized;
    private DateTime lastRun;
    private byte[] configFileChecksum = new byte[0];

    private Properties activemqProperties = new Properties();
    private Properties schedulerProperties;

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public String getStatusMsgTimeout() {
        return statusMsgTimeout;
    }

    @Override
    public Properties getActivemqProperties() {
        return activemqProperties;
    }

    @JsonIgnore
    @Override
    public LoginMode getLoginMode() {
        return LoginMode.valueOf(loginMode);
    }

    public String getLoginModeValue() {
        return loginMode;
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    public String getProviderUrl() {
        return providerUrl;
    }

    @Override
    public String getServerUrl() {
        return new MotechURL(this.serverUrl).toString();
    }

    @Override
    public String getServerHost() {
        return new MotechURL(this.serverUrl).getHost();
    }


    public boolean isPlatformInitialized() {
        return platformInitialized;
    }

    public void setPlatformInitialized(boolean platformInitialized) {
        this.platformInitialized = platformInitialized;
    }

    public String getUploadSize() {
        return uploadSize;
    }

    public Properties getSchedulerProperties() {
        return schedulerProperties;
    }

    public void setActivemqProperties(final Properties activemqProperties) {
        this.activemqProperties = activemqProperties;
    }

    public void setSchedulerProperties(final Properties schedulerProperties) {
        this.schedulerProperties = schedulerProperties;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    @JsonIgnore
    private void setLoginMode(LoginMode loginMode) {
        this.loginMode = loginMode == null ? StringUtils.EMPTY : loginMode.getName();
    }

    public void setLoginModeValue(String loginMode) {
        this.loginMode = loginMode;
    }


    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public void setStatusMsgTimeout(final String statusMsgTimeout) {
        this.statusMsgTimeout = statusMsgTimeout;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(final boolean cluster) {
        this.cluster = cluster;
    }

    public DateTime getLastRun() {
        return DateUtil.setTimeZoneUTC(lastRun);
    }

    public void setLastRun(final DateTime lastRun) {
        this.lastRun = lastRun;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setUploadSize(String uploadSize) {
        this.uploadSize = uploadSize;
    }

    public byte[] getConfigFileChecksum() {
        return Arrays.copyOf(configFileChecksum, configFileChecksum.length);
    }

    public void setConfigFileChecksum(final byte[] configFileChecksum) {
        this.configFileChecksum = Arrays.copyOf(configFileChecksum, configFileChecksum.length);
    }

    public void updateSettings(final MotechSettings settings) {
        setLanguage(settings.getLanguage());
        setStatusMsgTimeout(settings.getStatusMsgTimeout());
        setActivemqProperties(settings.getActivemqProperties());
        setSchedulerProperties(settings.getSchedulerProperties());
        setLoginMode(settings.getLoginMode());
        setProviderName(settings.getProviderName());
        setProviderUrl(settings.getProviderUrl());
        setServerUrl(settings.getServerUrl());
        setUploadSize(settings.getUploadSize());
    }

    public void updateFromProperties(final Properties props) {
        if (schedulerProperties == null || schedulerProperties.isEmpty()) {
            schedulerProperties = emptySchedulerProperties();
        }

        handleMiscProperties(props);
    }

    private void handleMiscProperties(Properties props) {
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            switch (key) {
                case MotechSettings.LANGUAGE:
                    setLanguage(value);
                    break;
                case MotechSettings.STATUS_MSG_TIMEOUT:
                    setStatusMsgTimeout(value);
                    break;
                case MotechSettings.LOGINMODE:
                    setLoginModeValue(value);
                    break;
                case MotechSettings.PROVIDER_NAME:
                    setProviderName(value);
                    break;
                case MotechSettings.PROVIDER_URL:
                    setProviderUrl(value);
                    break;
                case MotechSettings.SERVER_URL:
                    setServerUrl(value);
                    break;
                case MotechSettings.UPLOAD_SIZE:
                    setUploadSize(value);
                    break;
                default:
                    handleMiscProperty(key, value);
                    break;
            }
        }
    }

    private void handleMiscProperty(String key, String value) {
        for (Properties p : Arrays.asList(getSchedulerProperties())) {
            if (p.containsKey(key)) {
                p.put(key, value);
                break;
            }
        }
    }

    private Properties emptySchedulerProperties() {
        Properties props = new Properties();
        props.put(SCHEDULER_URL, "");
        return props;
    }
}
