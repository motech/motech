package org.motechproject.mds.web.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents single field of entity instance
 */
public class FieldRecord {

    public static final String FORM_VALUES = "mds.form.label.values";

    private String name;
    private String displayName;
    private String tooltip;
    private Object value;
    private TypeDto type;
    private List<MetadataDto> metadata;
    private List<SettingDto> settings;
    private Long id;
    private FieldValidationDto validation;
    private boolean required;

    public FieldRecord() {
        this(null, null, null, null);
    }

    public FieldRecord(String name, String displayName, Object value, TypeDto type) {
        this.name = name;
        this.displayName = displayName;
        setType(type);
        setValue(value);
    }

    public FieldRecord(FieldDto fieldDto) {
        this.name = fieldDto.getBasic().getName();
        this.displayName = fieldDto.getBasic().getDisplayName();
        this.id = fieldDto.getId();
        this.metadata = fieldDto.getMetadata();
        this.tooltip = fieldDto.getBasic().getTooltip();
        this.required = fieldDto.getBasic().isRequired();
        this.validation = fieldDto.getValidation();
        setSettings(fieldDto.getSettings());
        setType(fieldDto.getType());
        setValue(fieldDto.getBasic().getDefaultValue());
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getValue() {
        return value;
    }

    public final void setValue(Object value) {
        this.value = value;
        extendOptionsIfNecessary();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeDto getType() {
        return type;
    }

    public final void setType(TypeDto type) {
        this.type = type;
        extendOptionsIfNecessary();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MetadataDto> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<MetadataDto> metadata) {
        this.metadata = metadata;
    }

    public List<SettingDto> getSettings() {
        return settings;
    }

    @JsonIgnore
    public final SettingDto getSettingByName(String name) {
        if (CollectionUtils.isNotEmpty(settings)) {
            for (SettingDto setting : settings) {
                if (StringUtils.equals(setting.getName(), name)) {
                    return setting;
                }
            }
        }
        return null;
    }

    public final void setSettings(List<SettingDto> settings) {
        this.settings = new ArrayList<>();
        for (SettingDto setting : settings) {
            this.settings.add(setting.copy());
        }
        extendOptionsIfNecessary();
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
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

    private void extendOptionsIfNecessary() {
        // don't add null or empty string, only for list types
        if (!canExtendOptions()) {
            return;
        }

        // find the correct option
        SettingDto listValuesOption = getSettingByName(FORM_VALUES);

        // add the value
        if (listValuesOption != null) {
            if (listValuesOption.getValue() instanceof List) {
                // copy current values to avoid running into unmodifiable lists
                List listValues = new ArrayList((List) listValuesOption.getValue());
                // for lists, we add all not included
                if (List.class.isAssignableFrom(value.getClass())) {
                    for (Object objectFromValueList : (List) value) {
                        if (!listValues.contains(objectFromValueList)) {
                            listValues.add(objectFromValueList);
                        }
                    }
                // a case for single select comboboxes, just add the value
                } else {
                    if (!listValues.contains(value)) {
                        listValues.add(value);
                    }
                }
                listValuesOption.setValue(listValues);
            }
        }
    }

    private boolean canExtendOptions() {
        return value != null && !"".equals(value) && type != null && List.class.getName().equals(type.getTypeClass());
    }
}
