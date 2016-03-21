package org.motechproject.mds.web.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.util.TypeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.motechproject.mds.util.Constants.Settings.ALLOW_MULTIPLE_SELECTIONS;

/**
 * The <code>FieldRecordHelper</code> is an util class for {@link org.motechproject.mds.web.domain.FieldRecord} and
 * {@link org.motechproject.mds.web.domain.BasicFieldRecord} that wraps their setters and getters logic.
 */
public final class FieldRecordHelper {

    public static Object setValue(TypeDto type, List<SettingDto> settings, Object value) {
        Object newValue;
        if (type != null && List.class.getName().equals(type.getTypeClass())) {
            if (isMultiSelect(settings)) {
                newValue = TypeHelper.parse(stringifyEnums(value), List.class);
            } else {
                if (value instanceof List) {
                    // for a single object list we return the value(for single select inputs)
                    List list = (List) value;
                    newValue = (CollectionUtils.isNotEmpty(list)) ? stringifyEnums(list.get(0)) : null;
                } else {
                    List list = (List) TypeHelper.parse(stringifyEnums(value), List.class);
                    newValue = (CollectionUtils.isNotEmpty(list)) ? list.get(0) : list;
                }
            }
        } else {
            newValue = value;
        }

        return newValue;
    }

    public static SettingDto getSettingByName(List<SettingDto> settings, String name) {
        if (CollectionUtils.isNotEmpty(settings)) {
            for (SettingDto setting : settings) {
                if (StringUtils.equals(setting.getName(), name)) {
                    return setting;
                }
            }
        }
        return null;
    }

    public static List<SettingDto> setSettings(List<SettingDto> settings) {
        List<SettingDto> newSettings = new ArrayList<>();
        for (SettingDto setting : settings) {
            newSettings.add(setting.copy());
        }

        return newSettings;
    }


    private static Object stringifyEnums(Object val) {
        if (val instanceof Collection) {
            List<String> enumsAsStr = new ArrayList<>();
            for (Object obj : (Collection) val) {
                enumsAsStr.add(nullSafeToStr(obj));
            }
            return enumsAsStr;
        } else {
            return nullSafeToStr(val);
        }
    }

    private static String nullSafeToStr(Object obj) {
        return (obj == null) ? null : obj.toString();
    }

    private static boolean isMultiSelect(List<SettingDto> settings) {
        SettingDto setting = getSettingByName(settings, ALLOW_MULTIPLE_SELECTIONS);
        return setting != null && Boolean.TRUE.equals(setting.getValue());
    }

    private FieldRecordHelper() {
    }
}
