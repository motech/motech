package org.motechproject.admin.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Class for converting properties
 */
public final class ParamParser {

    private ParamParser() {
        // static utility class
    }

    /**
     * Builds list of settings option from given properties
     *
     * @param props properties from which list will be build
     * @return list of settings options from given properties
     */
    public static List<SettingsOption> parseProperties(Properties props) {
        List<SettingsOption> settingsList = new ArrayList<>();

        if (props != null) {
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                SettingsOption option = constructSettingsOption(entry);
                settingsList.add(option);
            }
        }

        return settingsList;
    }

    /**
     * Builds properties from given settings
     *
     * @param bundleSettings settings from which properties will be built
     * @return properties from given settings
     */
    public static Properties constructProperties(Settings bundleSettings) {
        Properties props = new Properties();
        for (SettingsOption option : bundleSettings.getSettings()) {
            props.put(option.getKey(), option.getValue());
        }
        return props;
    }

    /**
     * Builds settings option from given key and value
     *
     * @param key the key which will be set in settings option
     * @param value the value which will be set in settings option
     * @return settings option from given parameters
     */
    public static SettingsOption parseParam(String key, Object value) {
        SettingsOption settingsOption = new SettingsOption();

        settingsOption.setValue(value);
        settingsOption.setKey(key);
        settingsOption.setType(String.class.getSimpleName());

        return settingsOption;
    }

    private static SettingsOption constructSettingsOption(Map.Entry<Object, Object> entry) {
        return parseParam(String.valueOf(entry.getKey()), entry.getValue());
    }
}
