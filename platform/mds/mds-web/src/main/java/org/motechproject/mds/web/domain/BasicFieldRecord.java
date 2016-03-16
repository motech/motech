package org.motechproject.mds.web.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.web.util.FieldRecordHelper;

import java.util.List;

/**
 * Represents simplified field of entity instance.
 * This class is used for UI purposes, to display values of the fields in grids.
 */
public class BasicFieldRecord {

    private Long id;
    private String name;
    private Object value;
    private Object displayValue;
    private TypeDto type;
    private List<SettingDto> settings;

    public BasicFieldRecord() {
        this(null, null, null);
    }

    public BasicFieldRecord(String name, Object value, TypeDto type) {
        this.name = name;
        this.type = type;
        this.value = FieldRecordHelper.setValue(type, settings, value);
    }

    public BasicFieldRecord(FieldDto fieldDto) {
        this.id = fieldDto.getId();
        this.name = fieldDto.getBasic().getName();
        this.type = fieldDto.getType();
        this.settings = FieldRecordHelper.setSettings(fieldDto.getSettings());
        this.value = FieldRecordHelper.setValue(type, settings, fieldDto.getBasic().getDefaultValue());
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = FieldRecordHelper.setValue(type, settings, value);
    }

    public Object getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(Object displayValue) {
        this.displayValue = displayValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public TypeDto getType() {
        return type;
    }

    @JsonIgnore
    public void setType(TypeDto type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public List<SettingDto> getSettings() {
        return settings;
    }

    @JsonIgnore
    public final SettingDto getSettingByName(String name) {
        return FieldRecordHelper.getSettingByName(settings, name);
    }

    @JsonIgnore
    public void setSettings(List<SettingDto> settings) {
        this.settings = FieldRecordHelper.setSettings(settings);
    }
}
