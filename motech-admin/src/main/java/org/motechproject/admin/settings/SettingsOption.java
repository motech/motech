package org.motechproject.admin.settings;

import java.util.Map;

/**
 * Wrapper for easier use on the javascript side
 */
public class SettingsOption {

    private Object value;
    private String key;
    private String type;
    private String state;
    private Boolean mandatory;
    private String filename;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public SettingsOption() {
        // default constructor
    }

    public SettingsOption(Map.Entry<Object, Object> entry) {
        this.value = entry.getValue();
        this.type = entry.getValue().getClass().getSimpleName();
        this.key = String.valueOf(entry.getKey());
    }
}
