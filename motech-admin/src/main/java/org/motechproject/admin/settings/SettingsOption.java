package org.motechproject.admin.settings;

import java.util.Map;

public class SettingsOption {

    private Object value;
    private String key;
    private String type;
    private Boolean mandatory;

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

    public SettingsOption() {
        // default constructor
    }

    public SettingsOption(Map.Entry<Object, Object> entry) {
        this.value = entry.getValue();
        this.type = entry.getValue().getClass().getSimpleName();
        this.key = String.valueOf(entry.getKey());
    }
}
