package org.motechproject.email.model;

import org.motechproject.server.config.SettingsFacade;

import java.util.Objects;
import java.util.Properties;

public class SettingsDto {
    public static final String EMAIL_PROPERTIES_FILE_NAME = "motech-email.properties";
    public static final String MAIL_HOST_PROPERTY = "mail.host";
    public static final String MAIL_PORT_PROPERTY = "mail.port";

    private String host;
    private String port;

    public SettingsDto() {
        this(null, null);
    }

    public SettingsDto(SettingsFacade settingsFacade) {
        this(
                settingsFacade.getProperty(MAIL_HOST_PROPERTY, EMAIL_PROPERTIES_FILE_NAME),
                settingsFacade.getProperty(MAIL_PORT_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)
        );
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        properties.put(MAIL_HOST_PROPERTY, host);
        properties.put(MAIL_PORT_PROPERTY, port);

        return properties;
    }

    public SettingsDto(String host, String port) {
        this.host = host;
        this.port = port;
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

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
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

        return Objects.equals(this.host, other.host)
                && Objects.equals(this.port, other.port);
    }

    @Override
    public String toString() {
        return String.format("SettingsDto{host='%s', port='%s'}", host, port);
    }
}
