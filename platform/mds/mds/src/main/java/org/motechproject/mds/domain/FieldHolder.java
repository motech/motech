package org.motechproject.mds.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.Pair;
import org.motechproject.mds.util.TypeHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main purpose of this class is to provide an easy way to access values inside metadata and
 * settings related with the given field.
 */
public class FieldHolder {
    private Map<String, String> metadata = new HashMap<>();
    private Map<String, String> settings = new HashMap<>();

    public FieldHolder(Field field) {
        this(field.getMetadata(), field.getSettings());
    }

    public FieldHolder(FieldDto field) {
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

    /**
     * Retrieves metadata value of the given name from this field.
     *
     * @param name metadata key
     * @return value of the metadata entry
     */
    public String getMetadata(String name) {
        return getMetadata(name, null);
    }

    /**
     * Retrieves metadata value of the given name from this field. If there's no metadata
     * entry of the given name, a default value is returned.
     *
     * @param name metadata key
     * @param defaultValue default value to use, in case metadata entry is not present
     * @return value of the metadata entry, or default value
     */
    public String getMetadata(String name, String defaultValue) {
        return StringUtils.defaultIfBlank(metadata.get(name), defaultValue);
    }

    /**
     * Retrieves value of the setting, with the given name.
     *
     * @param name setting name
     * @return value of the setting
     */
    public String getSetting(String name) {
        return getSetting(name, null);
    }

    /**
     * Retrieves value of the setting, with the given name. If there's no setting
     * with the given name, a default value is returned.
     *
     * @param name setting name
     * @param defaultValue default value to use, in case given setting is not present
     * @return value of the setting
     */
    public String getSetting(String name, String defaultValue) {
        return StringUtils.defaultIfBlank(settings.get(name), defaultValue);
    }

    /**
     * Retrieves value of the setting, with the given name and parses the result to {@link boolean}.
     *
     * @param name setting name
     * @return value of the setting; true or false only
     */
    public boolean getSettingAsBoolean(String name) {
        return Boolean.parseBoolean(getSetting(name));
    }

    /**
     * Retrieves value of the setting, with the given name and parses the result into an array
     * of Strings. Comma mark (,) will be treated as a separator of the elements in the setting value.
     *
     * @param name setting name
     * @return value of the setting, in form of an array of Strings
     */
    public String[] getSettingAsArray(String name) {
        String[] values = TypeHelper.breakString(getSetting(name, ""));
        return Arrays.copyOf(values, values.length);
    }

}
