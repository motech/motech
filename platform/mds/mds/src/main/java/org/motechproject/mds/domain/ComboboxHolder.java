package org.motechproject.mds.domain;

import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.TypeHelper;
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
    private String enumName;
    private String[] values;

    public ComboboxHolder(Entity entity, Field field) {
        setEnumName(entity.getClassName(), field);

        for (FieldSetting setting : field.getSettings()) {
            setAttributes(setting.getDetails().getName(), setting.getValue());
        }
    }

    public ComboboxHolder(Object instance, FieldRecord fieldRecord) {
        setEnumName(instance.getClass().getName(), fieldRecord);

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

    public String getEnumName() {
        return enumName;
    }

    public String[] getValues() {
        return Arrays.copyOf(values, values.length);
    }

    private void setEnumName(String clazz, Field field) {
        FieldMetadata metadata = field.getMetadata(Constants.MetadataKeys.ENUM_CLASS_NAME);

        this.enumName = null != metadata
                ? metadata.getValue()
                : clazz + capitalize(field.getName());
    }

    private void setEnumName(String clazz, FieldRecord record) {
        MetadataDto metadata = record.getMetadata(Constants.MetadataKeys.ENUM_CLASS_NAME);

        this.enumName = null != metadata
                ? metadata.getValue()
                : clazz + capitalize(record.getName());
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
                this.values = TypeHelper.breakString(value);
                break;
            default:
        }
    }

}
