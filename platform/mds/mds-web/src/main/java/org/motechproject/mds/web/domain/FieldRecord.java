package org.motechproject.mds.web.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.mds.util.Constants.Settings.COMBOBOX_VALUES;

/**
 * Represents single field of entity instance.
 * This class is mainly used by the UI to pass and retrieve value and other properties of a field.
 */
public class FieldRecord extends BasicFieldRecord {

    private String displayName;
    private String tooltip;
    private String placeholder;
    private List<MetadataDto> metadata;
    private FieldValidationDto validation;
    private boolean required;
    private boolean nonEditable;
    private boolean nonDisplayable;

    public FieldRecord() {
        this(null, null, null);
    }

    public FieldRecord(String name, Object value, TypeDto type) {
        super(name, value, type);
    }

    public FieldRecord(FieldDto fieldDto) {
        super(fieldDto);
        this.metadata = fieldDto.getMetadata();
        this.tooltip = fieldDto.getBasic().getTooltip();
        this.displayName = fieldDto.getBasic().getDisplayName();
        this.nonEditable = fieldDto.isNonEditable();
        this.nonDisplayable = fieldDto.isNonDisplayable();
        this.placeholder = fieldDto.getBasic().getPlaceholder();
        this.required = fieldDto.getBasic().isRequired();
        this.validation = fieldDto.getValidation();
    }

    public List<MetadataDto> getMetadata() {
        return metadata;
    }

    public MetadataDto getMetadata(String key) {
        for (MetadataDto meta : metadata) {
            if (StringUtils.equals(key, meta.getKey())) {
                return meta;
            }
        }
        return null;
    }

    public void setMetadata(List<MetadataDto> metadata) {
        this.metadata = metadata;
    }

    @JsonIgnore(false)
    public TypeDto getType() {
        return super.getType();
    }

    @JsonIgnore(false)
    public void setType(TypeDto type) {
        super.setType(type);
        extendOptionsIfNecessary();
    }

    @JsonIgnore(false)
    @Override
    public void setSettings(List<SettingDto> settings) {
        super.setSettings(settings);
        extendOptionsIfNecessary();
    }

    @JsonIgnore(false)
    @Override
    public List<SettingDto> getSettings() {
        return super.getSettings();
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        extendOptionsIfNecessary();
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public FieldValidationDto getValidation() {
        return validation;
    }

    public void setValidation(FieldValidationDto validation) {
        this.validation = validation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isNonDisplayable() {
        return nonDisplayable;
    }

    public void setNonDisplayable(boolean nonDisplayable) {
        this.nonDisplayable = nonDisplayable;
    }

    public boolean isNonEditable() {
        return nonEditable;
    }

    public void setNonEditable(boolean nonEditable) {
        this.nonEditable = nonEditable;
    }

    private void extendOptionsIfNecessary() {
        // don't add null or empty string, only for list types
        if (!canExtendOptions()) {
            return;
        }

        // find the correct option
        SettingDto listValuesOption = getSettingByName(COMBOBOX_VALUES);

        // add the value
        if (listValuesOption != null) {
            if (listValuesOption.getValue() instanceof List) {
                // copy current values to avoid running into unmodifiable lists
                List listValues = new ArrayList((List) listValuesOption.getValue());
                // for lists, we add all not included
                if (List.class.isAssignableFrom(getValue().getClass())) {
                    for (Object objectFromValueList : (List) getValue()) {
                        if (!listValues.contains(objectFromValueList.toString())) {
                            listValues.add(objectFromValueList);
                        }
                    }
                    // a case for single select comboboxes, just add the value
                } else {
                    if (!listValues.contains(getValue().toString())) {
                        listValues.add(getValue());
                    }
                }
                listValuesOption.setValue(listValues);
            }
        }
    }

    private boolean canExtendOptions() {
        return getValue() != null && !"".equals(getValue()) && getType() != null && List.class.getName().equals(getType().getTypeClass());
    }
}
