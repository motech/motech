package org.motechproject.email.settings;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

public class SettingsDto {
    public static final String EMAIL_ADDITIONAL_PROPERTIES_FILE_NAME = "motech-email-additional.properties";
    public static final String EMAIL_PROPERTIES_FILE_NAME = "motech-email.properties";
    public static final String MAIL_HOST_PROPERTY = "mail.host";
    public static final String MAIL_PORT_PROPERTY = "mail.port";
    public static final String MAIL_USERNAME_PROPERTY = "mail.username";
    public static final String MAIL_PASSWORD_PROPERTY = "mail.password";
    public static final String MAIL_LOG_ADDRESS_PROPERTY = "mail.log.address";
    public static final String MAIL_LOG_SUBJECT_PROPERTY = "mail.log.subject";
    public static final String MAIL_LOG_BODY_PROPERTY = "mail.log.body";
    public static final String MAIL_LOG_PURGE_ENABLE_PROPERTY = "mail.log.purgeenable";
    public static final String MAIL_LOG_PURGE_TIME_PROPERY = "mail.log.purgetime";
    public static final String MAIL_LOG_PURGE_TIME_MULTIPLIER_PROPERTY = "mail.log.purgetimemultiplier";

    private String host;
    private String port;
    private String username;
    private String password;
    private String logAddress;
    private String logSubject;
    private String logBody;
    private String logPurgeEnable;
    private String logPurgeTime;
    private String logPurgeTimeMultiplier;
    private Map<String, String> additionalProperties;

    public SettingsDto() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public SettingsDto(SettingsFacade settingsFacade) {
        this(
                settingsFacade.getProperty(MAIL_HOST_PROPERTY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_PORT_PROPERTY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_USERNAME_PROPERTY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_PASSWORD_PROPERTY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getRawConfig(EMAIL_ADDITIONAL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_LOG_ADDRESS_PROPERTY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_LOG_SUBJECT_PROPERTY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_LOG_BODY_PROPERTY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_LOG_PURGE_ENABLE_PROPERTY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_LOG_PURGE_TIME_PROPERY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_LOG_PURGE_TIME_MULTIPLIER_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)
        );
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        properties.put(MAIL_HOST_PROPERTY, host);
        properties.put(MAIL_PORT_PROPERTY, port);
        properties.put(MAIL_USERNAME_PROPERTY, username);
        properties.put(MAIL_PASSWORD_PROPERTY, password);
        properties.put(MAIL_LOG_ADDRESS_PROPERTY, logAddress);
        properties.put(MAIL_LOG_SUBJECT_PROPERTY, logSubject);
        properties.put(MAIL_LOG_BODY_PROPERTY, logBody);
        properties.put(MAIL_LOG_PURGE_ENABLE_PROPERTY, logPurgeEnable);
        properties.put(MAIL_LOG_PURGE_TIME_PROPERY, logPurgeTime);
        properties.put(MAIL_LOG_PURGE_TIME_MULTIPLIER_PROPERTY, logPurgeTimeMultiplier);
        return properties;
    }

    public SettingsDto(String host, String port, String user, String password, InputStream additionalProperties, // NO CHECKSTYLE More than 7 parameters (found 8).
                       String logAddress, String logSubject, String logBody, String logPurgeEnable,
                       String logPurgeTime, String logPurgeTimeMultiplier) {
        this(host, port, user, password, new HashMap<String, String>(), logAddress, logSubject, logBody, logPurgeEnable, logPurgeTime, logPurgeTimeMultiplier);
        try {
            Properties props = new Properties();
            props.load(additionalProperties);
            if (props != null) {
                for (Entry<Object, Object> entry : props.entrySet()) {
                    this.additionalProperties.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        } catch (IOException e) {
            throw new MotechException("Error loading raw file config to properties", e);
        }
    }

    public SettingsDto(String host, String port, String user, String password, Properties additionalProperties, // NO CHECKSTYLE More than 7 parameters (found 8).
                       String logAddress, String logSubject, String logBody, String logPurgeEnable,
                       String logPurgeTime, String logPurgeTimeMultiplier) {
        this(host, port, user, password, new HashMap<String, String>(), logAddress, logSubject, logBody, logPurgeEnable, logPurgeTime, logPurgeTimeMultiplier);
        if (additionalProperties != null) {
            for (Entry<Object, Object> entry : additionalProperties.entrySet()) {
                this.additionalProperties.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
    }

    public SettingsDto(String host, String port, String user, String password, Map<String, String> additionalProperties, String logAddress, String logSubject, String logBody, String logPurgeEnable) {
        this(host, port, user, password, CollectionUtils.isEmpty(additionalProperties) ? new HashMap<String, String>() : additionalProperties, logAddress, logSubject, logBody, logPurgeEnable, "0", "0");
    }

    public SettingsDto(String host, String port, String user, String password, Map<String, String> additionalProperties, // NO CHECKSTYLE More than 7 parameters (found 8).
                       String logAddress, String logSubject, String logBody, String logPurgeEnable,
                       String logPurgeTime, String logPurgeTimeMultiplier) {
        this.host = host;
        this.port = port;
        this.username = user;
        this.password = password;
        this.additionalProperties = CollectionUtils.isEmpty(additionalProperties) ? new HashMap<String, String>() : additionalProperties;
        this.logAddress = logAddress;
        this.logSubject = logSubject;
        this.logBody = logBody;
        this.logPurgeEnable = logPurgeEnable;
        this.logPurgeTime = logPurgeTime;
        this.logPurgeTimeMultiplier = logPurgeTimeMultiplier;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> getAdditionalProperties() {
        if (this.additionalProperties == null) {
            this.additionalProperties = new HashMap<>();
        }
        return this.additionalProperties;
    }

    public void setAdditionalProperties(Map<String, String> additionalProperties) {
        if (additionalProperties == null) {
            this.additionalProperties = new HashMap<String, String>();
        } else {
            this.additionalProperties = additionalProperties;
        }
    }

    public String getLogAddress() {
        return logAddress;
    }

    public void setLogAddress(String logAddress) {
        this.logAddress = logAddress;
    }

    public String getLogSubject() {
        return logSubject;
    }

    public void setLogSubject(String logSubject) {
        this.logSubject = logSubject;
    }

    public String getLogBody() {
        return logBody;
    }

    public void setLogBody(String logBody) {
        this.logBody = logBody;
    }

    public String getLogPurgeEnable() {
        return logPurgeEnable;
    }

    public void setLogPurgeEnable(String logPurgeEnable) {
        this.logPurgeEnable = logPurgeEnable;
    }

    public String getLogPurgeTime() {
        return logPurgeTime;
    }

    public void setLogPurgeTime(String logPurgeTime) {
        this.logPurgeTime = logPurgeTime;
    }

    public String getLogPurgeTimeMultiplier() {
        return logPurgeTimeMultiplier;
    }

    public void setLogPurgeTimeMultiplier(String logPurgeTimeMultiplier) {
        this.logPurgeTimeMultiplier = logPurgeTimeMultiplier;
    }

    @JsonIgnore
    public Properties getAdditionalProps() {
        Properties props = new Properties();
        props.putAll(this.additionalProperties);
        return props;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, username, password, additionalProperties,  logAddress, logSubject, logBody, logPurgeEnable, logPurgeTime, logPurgeTimeMultiplier);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final SettingsDto other = (SettingsDto) obj;

        return compareFields(other);
    }

    @Override
    public String toString() {
        return String.format(
                "SettingsDto{host='%s', port='%s', username='%s', password='%s', additionalProperties='%s', logAddress='%s', logSubject='%s', logBody='%s', logPurgeEnable='%s', logPurgeTime='%s', logPurgeTimeMultiplier='%s'}",
                host, port, username, password, additionalProperties, logAddress, logSubject, logBody, logPurgeEnable, logPurgeTime, logPurgeTimeMultiplier);
    }

    private Boolean compareFields(SettingsDto other) {
        if (!Objects.equals(this.host, other.host)) {
            return false;
        } else if (!Objects.equals(this.port, other.port)) {
            return false;
        } else if (!Objects.equals(this.username, other.username)) {
            return false;
        } else if (!Objects.equals(this.logAddress, other.logAddress)) {
            return false;
        } else if (!Objects.equals(this.logSubject, other.logSubject)) {
            return false;
        } else if (!Objects.equals(this.logBody, other.logBody)) {
            return false;
        } else if (!Objects.equals(this.logPurgeEnable, other.logPurgeEnable)) {
            return false;
        } else if (!Objects.equals(this.logPurgeTime, other.logPurgeTime)) {
            return false;
        } else if (!Objects.equals(this.logPurgeTimeMultiplier, other.logPurgeTimeMultiplier)) {
            return false;
        }
        return true;
    }
}
