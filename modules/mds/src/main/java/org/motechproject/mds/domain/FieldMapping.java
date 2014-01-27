package org.motechproject.mds.domain;

import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>FieldMapping</code> class contains information about a single field
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class FieldMapping {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private EntityMapping entity;

    @Persistent
    private String displayName;

    @Persistent
    private String name;

    @Persistent
    private boolean required;

    @Persistent
    private String defaultValue;

    @Persistent
    private String tooltip;

    @Persistent
    private AvailableFieldTypeMapping type;

    @Persistent(mappedBy = "field")
    private List<FieldMetadataMapping> metadata;

    public FieldMapping(FieldDto field, EntityMapping entity, AvailableFieldTypeMapping type) {
        this.entity = entity;
        this.type = type;
        this.displayName = field.getBasic().getDisplayName();
        this.name = field.getBasic().getName();
        this.required = field.getBasic().isRequired();
        this.tooltip = field.getBasic().getTooltip();

        if (field.getBasic().getDefaultValue() != null) {
            this.defaultValue = field.getBasic().getDefaultValue().toString();
        }

        metadata = new ArrayList<>();

        if (null != field.getMetadata()) {
            for (MetadataDto meta : field.getMetadata()) {
                metadata.add(new FieldMetadataMapping(this, meta.getKey(), meta.getValue()));
            }
        }
    }

    public FieldDto toDto() {
        FieldBasicDto fieldBasic = new FieldBasicDto(displayName, name, required, defaultValue, tooltip);
        List<MetadataDto> metadataDto = new ArrayList<>();

        if (null != metadata) {
            for (FieldMetadataMapping meta : metadata) {
                metadataDto.add(meta.toDto());
            }
        }

        return new FieldDto(id, entity.getId(), type.toDto().getType(), fieldBasic, metadataDto, null, null);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public EntityMapping getEntity() {
        return entity;
    }

    public void setEntity(EntityMapping entity) {
        this.entity = entity;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public AvailableFieldTypeMapping getType() {
        return type;
    }

    public void setType(AvailableFieldTypeMapping type) {
        this.type = type;
    }

    public FieldMapping update(FieldDto field) {
        this.setDisplayName(field.getBasic().getDisplayName());
        this.setName(field.getBasic().getName());
        this.setRequired(field.getBasic().isRequired());
        this.setTooltip(field.getBasic().getTooltip());

        if (field.getBasic().getDefaultValue() != null) {
            this.setDefaultValue(field.getBasic().getDefaultValue().toString());
        }

        metadata = new ArrayList<>();

        if (null != field.getMetadata()) {
            for (MetadataDto meta : field.getMetadata()) {
                metadata.add(new FieldMetadataMapping(this, meta.getKey(), meta.getValue()));
            }
        }

        return this;
    }
}
