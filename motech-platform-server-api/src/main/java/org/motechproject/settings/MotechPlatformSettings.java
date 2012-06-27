package org.motechproject.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Locale;
import java.util.Properties;

public class MotechPlatformSettings {

    @Autowired
    @Qualifier("motechSettings")
    private Properties properties;

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        if (properties != null) {
            return properties.getProperty(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public String getDefaultLanguage() {
        return getProperty("language", "en");
    }

    public Locale getDefaultLocale() {
        return new Locale(getDefaultLanguage());
    }

    private static MotechPlatformSettings instance = new MotechPlatformSettings();

    public static MotechPlatformSettings getInstance() {
        return instance;
    }

    private MotechPlatformSettings() {
        // private constructor
    }
}
