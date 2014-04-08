package org.motechproject.mds.domain;

import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.web.domain.FieldRecord;

import java.util.Arrays;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;

/**
 * The main purpose of this class is to find out what kind of type should be used when the field
 * will be added to the class definition.
 */
public class ComboboxHolder {
    private boolean allowMultipleSelections;
    private boolean allowUserSupplied;
    private String enumFullName;
    private String[] values;

    public ComboboxHolder(Entity entity, Field field) {
        setEnumFullName(entity.getClassName(), field.getName());

        for (FieldSetting setting : field.getSettings()) {
            setAttributes(setting.getDetails().getName(), setting.getValue());
        }
    }

    public ComboboxHolder(Object instance, FieldRecord fieldRecord) {
        setEnumFullName(instance.getClass().getName(), fieldRecord.getName());

        for (SettingDto setting : fieldRecord.getSettings()) {
            setAttributes(setting.getName(), setting.getValueAsString());
        }
    }

    public boolean isStringList() {
        return allowUserSupplied && allowMultipleSelections;
    }

    public boolean isEnumList() {
        return !allowUserSupplied && allowMultipleSelections;
    }

    public boolean isString() {
        return allowUserSupplied && !allowMultipleSelections;
    }

    public boolean isEnum() {
        return !allowUserSupplied && !allowMultipleSelections;
    }

    public String getEnumFullName() {
        return enumFullName;
    }

    public String getEnumSimpleName() {
        return ClassName.getSimpleName(enumFullName);
    }

    public String[] getValues() {
        return Arrays.copyOf(values, values.length);
    }

    private void setEnumFullName(String clazz, String fieldName) {
        this.enumFullName = clazz + capitalize(fieldName) + "Enum";
    }

    private void setAttributes(String name, String value) {
        String notBlankValue = defaultIfBlank(value, "");

        switch (name) {
            case "mds.form.label.allowUserSupplied":
                this.allowUserSupplied = Boolean.parseBoolean(notBlankValue);
                break;
            case "mds.form.label.allowMultipleSelections":
                this.allowMultipleSelections = Boolean.parseBoolean(notBlankValue);
                break;
            case "mds.form.label.values":
                this.values = notBlankValue.replaceAll("(\\[|\\]|\")", "").split("(,|\n)", -1);
                break;
            default:
        }
    }

}
