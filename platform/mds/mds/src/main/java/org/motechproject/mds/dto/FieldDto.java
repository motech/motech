package org.motechproject.mds.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.util.Constants;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The <code>FieldDto</code> class contains information about an existing field in an entity.
 */
public class FieldDto {
    private Long id;
    private Long entityId;
    private TypeDto type;
    private FieldBasicDto basic;
    private boolean readOnly;
    private boolean nonEditable;
    private boolean nonDisplayable;
    private boolean uiChanged;
    private List<MetadataDto> metadata;
    private FieldValidationDto validation;
    private List<SettingDto> settings;
    private List<LookupDto> lookups;

    public FieldDto() {
        this(null, null, null, null, false, false, true, null, null, null, null);
    }

    public FieldDto(String name, String displayName, TypeDto type) {
        this(name, displayName, type, false, null, null, null);
    }

    public FieldDto(String name, String displayName, TypeDto type, boolean required) {
        this(name, displayName, type, required, null, null, null);
    }

    public FieldDto(String name, String displayName, TypeDto type, boolean required, Object defaultValue) {
        this(name, displayName, type, required, defaultValue, null, null);
    }

    public FieldDto(String name, String displayName, TypeDto type, boolean required, Object defaultValue, String tooltip, String placeholder) {
        this(null, null, type, null, false, false, true, null, null, null, null);
        this.basic = new FieldBasicDto(displayName, name, required, defaultValue, tooltip, placeholder);
    }

    public FieldDto(Long id, Long entityId, TypeDto type, FieldBasicDto basic, boolean readOnly,
                    List<MetadataDto> metadata, FieldValidationDto validation,
                    List<SettingDto> settings, List<LookupDto> lookups) {
        this(id, entityId, type, basic, readOnly, false, true, metadata, validation, settings, lookups);
    }

    public FieldDto(Long id, Long entityId, TypeDto type, FieldBasicDto basic, boolean readOnly, boolean nonEditable,
                    boolean nonDisplayable, List<MetadataDto> metadata, FieldValidationDto validation,
                    List<SettingDto> settings, List<LookupDto> lookups) {
        this(id, entityId, type, basic, readOnly, nonEditable, nonDisplayable, false, metadata, validation, settings,
                lookups);
    }

    public FieldDto(Long id, Long entityId, TypeDto type, FieldBasicDto basic, boolean readOnly, boolean nonEditable,
                    boolean nonDisplayable, boolean uiChanged, List<MetadataDto> metadata, FieldValidationDto validation,
                    List<SettingDto> settings, List<LookupDto> lookups) {
        this.id = id;
        this.entityId = entityId;
        this.type = type;
        this.basic = basic;
        this.readOnly = readOnly;
        this.nonEditable = nonEditable;
        this.nonDisplayable = nonDisplayable;
        this.uiChanged = uiChanged;
        this.validation = validation;
        this.metadata = CollectionUtils.isEmpty(metadata)
                ? new LinkedList<MetadataDto>()
                : metadata;
        this.settings = CollectionUtils.isEmpty(settings)
                ? new LinkedList<SettingDto>()
                : settings;
        this.lookups = CollectionUtils.isEmpty(lookups)
                ? new LinkedList<LookupDto>()
                : lookups;
    }

    public FieldDto(Long id, Long entityId, TypeDto type, FieldBasicDto basic, boolean readOnly, FieldValidationDto validation) {
        this.id = id;
        this.entityId = entityId;
        this.type = type;
        this.basic = basic;
        this.validation = validation;
        this.readOnly = readOnly;
    }

    public boolean multiSelect() {
        for (SettingDto setting : settings) {
            if (setting.multiSelect()) {
                return true;
            }
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public TypeDto getType() {
        return type;
    }

    public void setType(TypeDto type) {
        this.type = type;
    }

    public FieldBasicDto getBasic() {
        return basic;
    }

    public void setBasic(FieldBasicDto basic) {
        this.basic = basic;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void addEmptyMetadata() {
        addMetadata(new MetadataDto());
    }

    public void addMetadata(MetadataDto metadata) {
        getMetadata().add(metadata);
    }

    public void removeMetadata(Integer idx) {
        if (null != idx && idx < getMetadata().size()) {
            getMetadata().remove(idx.intValue());
        }
    }

    public List<MetadataDto> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
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
        this.metadata = CollectionUtils.isEmpty(metadata)
                ? new LinkedList<MetadataDto>()
                : metadata;
    }

    public FieldValidationDto getValidation() {
        return validation;
    }

    public void setValidation(FieldValidationDto validation) {
        this.validation = validation;
    }

    @JsonIgnore
    public SettingDto getSetting(String name) {
        SettingDto found = null;

        for (SettingDto setting : getSettings()) {
            if (setting.getName().equalsIgnoreCase(name)) {
                found = setting;
                break;
            }
        }

        return found;
    }

    @JsonIgnore
    public String getSettingsValueAsString(String name) {
        SettingDto setting = getSetting(name);
        return setting == null ? null : setting.getValueAsString();
    }

    public boolean isVersionField() {
        MetadataDto md = getMetadata(Constants.MetadataKeys.VERSION_FIELD);
        String metadataValue = md == null ? null : md.getValue();
        if (StringUtils.isNotBlank(metadataValue)) {
            return new Boolean(metadataValue);
        }

        return false;
    }

    public List<SettingDto> getSettings() {
        if (settings == null) {
            settings = new ArrayList<>();
        }
        return settings;
    }

    public void setSettings(List<SettingDto> settings) {
        this.settings = settings;
    }

    public List<LookupDto> getLookups() {
        return lookups;
    }

    public void setLookups(List<LookupDto> lookups) {
        this.lookups = lookups;
    }

    public boolean isNonEditable() {
        return nonEditable;
    }

    public void setNonEditable(boolean nonEditable) {
        this.nonEditable = nonEditable;
    }

    public boolean isNonDisplayable() {
        return nonDisplayable;
    }

    public void setNonDisplayable(boolean nonDisplayable) {
        this.nonDisplayable = nonDisplayable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean isUiChanged() {
        return uiChanged;
    }

    public void setUiChanged(boolean uiChanged) {
        this.uiChanged = uiChanged;
    }
}
