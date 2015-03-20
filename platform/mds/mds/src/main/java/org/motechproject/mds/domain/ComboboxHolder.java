package org.motechproject.mds.domain;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Pair;

import java.util.List;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.motechproject.mds.util.Constants.MetadataKeys.ENUM_CLASS_NAME;

/**
 * The main purpose of this class is to make it easier to find out what kind of type
 * should be used when the field is added to the class definition.
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

    /**
     * @return true, if this combobox allows user supplied values and allows selecting multiple values; false otherwise
     */
    public boolean isStringList() {
        return isAllowUserSupplied() && isAllowMultipleSelections();
    }

    /**
     * @return true, if this combobox does not allow user supplied values and allows selecting multiple values; false otherwise
     */
    public boolean isEnumList() {
        return !isAllowUserSupplied() && isAllowMultipleSelections();
    }

    /**
     * @return true, if this combobox allows user supplied values and allows selecting only single value; false otherwise
     */
    public boolean isString() {
        return isAllowUserSupplied() && !isAllowMultipleSelections();
    }

    /**
     * @return true, if this combobox does not allow user supplied values and allows selecting only single value; false otherwise
     */
    public boolean isEnum() {
        return !isAllowUserSupplied() && !isAllowMultipleSelections();
    }

    /**
     * @return enum name, specified in the field metadata, or default name, if not explicitly provided
     */
    public String getEnumName() {
        return getMetadata(ENUM_CLASS_NAME, defaultEnumName);
    }

    /**
     * @return true, if this combobox allows user supplied values; false otherwise
     */
    public boolean isAllowUserSupplied() {
        return getSettingAsBoolean(Constants.Settings.ALLOW_USER_SUPPLIED);
    }

    /**
     * @return true, if this combobox allows selecting multiple values; false otherwise
     */
    public boolean isAllowMultipleSelections() {
        return getSettingAsBoolean(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS);
    }

    /**
     * @return an array of possible values for this combobox
     */
    public String[] getValues() {
        return getSettingAsArray(Constants.Settings.COMBOBOX_VALUES);
    }


    /**
     * @return fully qualified class name, of the actual java type of this combobox field
     */
    public String getUnderlyingType() {
        if (isString() || isStringList()) {
            return String.class.getName();
        } else {
            return getEnumName();
        }
    }

    /**
     * @return true, if this combobox is handled by a list type in the backend; false otherwise
     */
    public boolean isList() {
        return isEnumList() || isStringList();
    }

    /**
     * @return fully qualified class name, that handles this combobox in the backend
     */
    public String getTypeClassName() {
        if (isList()) {
            return List.class.getName();
        } else if (isEnum()) {
            return getEnumName();
        } else {
            return String.class.getName();
        }
    }
}
