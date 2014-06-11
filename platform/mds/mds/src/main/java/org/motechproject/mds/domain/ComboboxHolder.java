package org.motechproject.mds.domain;

import org.motechproject.mds.web.domain.FieldRecord;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.motechproject.mds.util.Constants.MetadataKeys.ENUM_CLASS_NAME;

/**
 * The main purpose of this class is to find out what kind of type should be used when the field
 * will be added to the class definition.
 */
public class ComboboxHolder extends FieldHolder {
    private String defaultEnumName;

    public ComboboxHolder(Field field) {
        this(field.getEntity(), field);
    }

    public ComboboxHolder(Entity entity, Field field) {
        super(field);

        this.defaultEnumName = entity.getClassName() + capitalize(field.getName());
    }

    public ComboboxHolder(Object instance, FieldRecord field) {
        super(field);

        this.defaultEnumName = instance.getClass().getName() + capitalize(field.getName());
    }

    public boolean isStringList() {
        return isAllowUserSupplied() && isAllowMultipleSelections();
    }

    public boolean isEnumList() {
        return !isAllowUserSupplied() && isAllowMultipleSelections();
    }

    public boolean isString() {
        return isAllowUserSupplied() && !isAllowMultipleSelections();
    }

    public boolean isEnum() {
        return !isAllowUserSupplied() && !isAllowMultipleSelections();
    }

    public String getEnumName() {
        return getMetadata(ENUM_CLASS_NAME, defaultEnumName);
    }

    public boolean isAllowUserSupplied() {
        return getSettingAsBoolean("mds.form.label.allowUserSupplied");
    }

    public boolean isAllowMultipleSelections() {
        return getSettingAsBoolean("mds.form.label.allowMultipleSelections");
    }

    public String[] getValues() {
        return getSettingAsArray("mds.form.label.values");
    }

}
