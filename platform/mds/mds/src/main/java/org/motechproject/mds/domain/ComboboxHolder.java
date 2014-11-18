package org.motechproject.mds.domain;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Pair;

import java.util.List;

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

    public ComboboxHolder(EntityDto entity, FieldDto field) {
        super(field);
        this.defaultEnumName = entity.getClassName() + capitalize(field.getBasic().getName());
    }

    public ComboboxHolder(Class<?> entityClass, FieldDto field) {
        super(field);
        this.defaultEnumName = entityClass.getName() + capitalize(field.getBasic().getName());
    }

    public ComboboxHolder(List<? extends Pair<String, String>> metadata,
                          List<? extends Pair<String, ?>> settings,
                          String defaultEnumName) {
        super(metadata, settings);
        this.defaultEnumName = defaultEnumName;
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
        return getSettingAsBoolean(Constants.Settings.ALLOW_USER_SUPPLIED);
    }

    public boolean isAllowMultipleSelections() {
        return getSettingAsBoolean(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS);
    }

    public String[] getValues() {
        return getSettingAsArray(Constants.Settings.COMBOBOX_VALUES);
    }

    public String getUnderlyingType() {
        if (isString() || isStringList()) {
            return String.class.getName();
        } else {
            return getEnumName();
        }
    }
}
