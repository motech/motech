package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.CollectionUtils;

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
    private List<MetadataDto> metadata;
    private FieldValidationDto validation;
    private List<SettingDto> settings;

    public FieldDto() {
        this(null, null, null, null, null, null, null);
    }

    public FieldDto(Long id, Long entityId, TypeDto type, FieldBasicDto basic,
                    List<MetadataDto> metadata, FieldValidationDto validation,
                    List<SettingDto> settings) {
        this.id = id;
        this.entityId = entityId;
        this.type = type;
        this.basic = basic;
        this.validation = validation;
        this.metadata = CollectionUtils.isEmpty(metadata)
                ? new LinkedList<MetadataDto>()
                : metadata;
        this.settings = CollectionUtils.isEmpty(settings)
                ? new LinkedList<SettingDto>()
                : settings;
    }

    public FieldDto(Long id, Long entityId, TypeDto type, FieldBasicDto basic) {
        this.id = id;
        this.entityId = entityId;
        this.type = type;
        this.basic = basic;
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

    public void addEmptyMetadata() {
        metadata.add(new MetadataDto());
    }

    public void removeMetadata(Integer idx) {
        if (null != idx && idx < metadata.size()) {
            metadata.remove(idx.intValue());
        }
    }

    public List<MetadataDto> getMetadata() {
        return metadata;
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

    public List<SettingDto> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingDto> settings) {
        this.settings = settings;
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
}
