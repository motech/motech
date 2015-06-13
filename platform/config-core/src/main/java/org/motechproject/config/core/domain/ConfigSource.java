package org.motechproject.config.core.domain;


import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.MotechConfigurationException;

/**
 * Represents the source from which MOTECH configuration should be read.
 */
public final class ConfigSource {
    public static final ConfigSource FILE = new ConfigSource("FILE");
    public static final ConfigSource UI = new ConfigSource("UI");
    private String name;

    private ConfigSource(String name) {
        this.name = name;
    }

    /**
     * Creates proper object of {@code ConfigSource} class for given name. The correct values are "UI" and "FILE".
     * If name isn't one of above MotechConfigurationException will be thrown.
     *
     * @param name  the name of the configuration source, null and blank String treated as "UI"
     * @throws org.motechproject.config.core.MotechConfigurationException when name is neither "FILE" nor "UI"
     * @return proper instance of {@code ConfigSource}
     */
    public static ConfigSource valueOf(String name) {
        if (!StringUtils.isNotBlank(name) || name.trim().equalsIgnoreCase(UI.name)) {
            return UI;
        }
        if (FILE.name.equalsIgnoreCase(name.trim())) {
            return FILE;
        }
        throw new MotechConfigurationException("Config source [" + name + "] not supported.");
    }

    /**
     * Checks whether given name is a name of supported configuration source.
     *
     * @param name  the name to be checked
     * @return true if name is valid, false otherwise
     */
    public static boolean isValid(String name) {
        try {
            valueOf(name);
            return true;
        } catch (MotechConfigurationException e) {
            return false;
        }
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

    /**
     * Checks whether this configuration source is FILE or not.
     *
     * @return true if this configuration source is file, false otherwise
     */
    public boolean isFile() {
        return this == FILE;
    }
}

