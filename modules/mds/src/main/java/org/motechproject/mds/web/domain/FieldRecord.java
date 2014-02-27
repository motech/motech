package org.motechproject.mds.web.domain;

import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;

import java.util.List;

/**
 * Represents single field of entity instance
 */
public class FieldRecord {
    private String name;
    private String displayName;
    private String tooltip;
    private Object value;
    private TypeDto type;
    private List<MetadataDto> metadata;
    private List<SettingDto> settings;
    private Long id;

    public FieldRecord() {
        this(null, null, null, null);
    }

    public FieldRecord(String name, String displayName, Object value, TypeDto type) {
        this.name = name;
        this.displayName = displayName;
        this.value = value;
        this.type = type;
    }

    public FieldRecord(FieldDto fieldDto) {
        this.name = fieldDto.getBasic().getName();
        this.displayName = fieldDto.getBasic().getDisplayName();
        this.value = fieldDto.getBasic().getDefaultValue();
        this.type = fieldDto.getType();
        this.id = fieldDto.getId();
        this.metadata = fieldDto.getMetadata();
        this.settings = fieldDto.getSettings();
        this.tooltip = fieldDto.getBasic().getTooltip();
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

    public void setValue(Object value) {
        this.value = value;
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

    public void setType(TypeDto type) {
        this.type = type;
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

    public void setSettings(List<SettingDto> settings) {
        this.settings = settings;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}
