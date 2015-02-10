package org.motechproject.admin.settings;

import java.util.Map;

/**
 * Class for storing one settings option, it is representing one settings option
 * on Admin settings UI.
 */
public class SettingsOption {

    private Object value;
    private String key;

    /**
     * @return this settings value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value of this setting
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the key to which this option corresponds
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to which this option corresponds
     */
    public void setKey(String key) {
        this.key = key;
    }

    public SettingsOption() {
        // default constructor
    }

    public SettingsOption(Map.Entry<Object, Object> entry) {
        this.value = entry.getValue();
        this.key = String.valueOf(entry.getKey());
    }
}
