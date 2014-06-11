package org.motechproject.mds.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.util.Pair;
import org.motechproject.mds.util.TypeHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main purpose of this class is to give a easy way to access values inside metadata and
 * settings related with the given field.
 */
public class FieldHolder {
    private Map<String, String> metadata = new HashMap<>();
    private Map<String, String> settings = new HashMap<>();

    public FieldHolder(Field field) {
        this(field.getMetadata(), field.getSettings());
    }

    protected FieldHolder(List<? extends Pair<String, String>> metadata,
                          List<? extends Pair<String, ?>> settings) {
        for (Pair<String, String> entry : metadata) {
            this.metadata.put(entry.getKey(), entry.getValue());
        }

        for (Pair<String, ?> entry : settings) {
            Object value = entry.getValue();
            String valueAsString = null == value ? null : value.toString();

            this.settings.put(entry.getKey(), valueAsString);
        }
    }

    public String getMetadata(String name) {
        return getMetadata(name, null);
    }

    public String getMetadata(String name, String defaultValue) {
        return StringUtils.defaultIfBlank(metadata.get(name), defaultValue);
    }

    public String getSetting(String name) {
        return getSetting(name, null);
    }

    public String getSetting(String name, String defaultValue) {
        return StringUtils.defaultIfBlank(settings.get(name), defaultValue);
    }

    public boolean getSettingAsBoolean(String name) {
        return Boolean.parseBoolean(getSetting(name));
    }

    public String[] getSettingAsArray(String name) {
        String[] values = TypeHelper.breakString(getSetting(name, ""));
        return Arrays.copyOf(values, values.length);
    }

}
