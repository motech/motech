package org.motechproject.config.domain;


import org.motechproject.config.MotechConfigurationException;

/**
 * Represents the source from which configuration is read.
 */
public final class ConfigSource {
    public static final ConfigSource FILE = new ConfigSource("FILE");
    public static final ConfigSource UI = new ConfigSource("UI");
    private String name;

    private ConfigSource(String name) {
        this.name = name;
    }

    public static ConfigSource valueOf(String name) {
        if (name == null || name.equalsIgnoreCase(UI.name)) {
            return UI;
        }
        if (FILE.name.equalsIgnoreCase(name)) {
            return FILE;
        }
        throw new MotechConfigurationException("Config source [" + name +"] not supported.");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConfigSource{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }
}

