package org.motechproject.admin.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ParamParser {

    private ParamParser() {
        // static utility class
    }

    public static List<SettingsOption> parseProperties(Properties props) {
        List<SettingsOption> settingsList = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            SettingsOption option = constructSettingsOption(entry);
            settingsList.add(option);
        }
        return settingsList;
    }

    public static  Properties constructProperties(Settings bundleSettings) {
        Properties props = new Properties();
        for (SettingsOption option : bundleSettings.getSettings()) {
            props.put(option.getKey(), option.getValue());
        }
        return props;
    }

    public static  SettingsOption parseParam(String key, Object value) {
        SettingsOption settingsOption = new SettingsOption();

        settingsOption.setValue(value);
        settingsOption.setKey(key);
        settingsOption.setType(String.class.getSimpleName());

        return settingsOption;
    }

    public static  void convertName(SettingsOption option) {
        option.setKey(NameConversionUtil.convertName(option.getKey()));
    }

    public static  void convertNames(List<SettingsOption> options) {
        for (SettingsOption option : options) {
            convertName(option);
        }
    }

    private static  SettingsOption constructSettingsOption(Map.Entry<Object, Object> entry) {
        return parseParam(String.valueOf(entry.getKey()), entry.getValue());
    }
}
