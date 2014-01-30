package org.motechproject.mds.domain;

import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
    @Element(dependent = "TRUE")
    private List<FieldMetadataMapping> metadata;

    @Persistent(dependent = "TRUE")
    private TypeValidationMapping validation;

    public FieldMapping() {
    }

    public FieldMapping(FieldDto field, EntityMapping entity, AvailableFieldTypeMapping type, TypeValidationMapping validation) {
        this.entity = entity;
        this.type = type;
        this.validation = validation;
        this.displayName = field.getBasic().getDisplayName();
        this.name = field.getBasic().getName();
        this.required = field.getBasic().isRequired();
        this.tooltip = field.getBasic().getTooltip();

        if (field.getBasic().getDefaultValue() != null) {
            this.defaultValue = field.getBasic().getDefaultValue().toString();
        }

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

        TypeDto basicType = new TypeDto(type.toDto().getType().getDisplayName(), type.toDto().getType().getDisplayName(), type.toDto().getType().getTypeClass());
        FieldValidationDto validationDto = (validation == null) ? null : validation.toDto();

        return new FieldDto(id, entity.getId(), basicType, fieldBasic, metadataDto, validationDto, null);
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

    public TypeValidationMapping getValidation() {
        return validation;
    }

    public void setValidation(TypeValidationMapping validation) {
        this.validation = validation;
    }

    public List<FieldMetadataMapping> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return metadata;
    }

    public void setMetadata(List<FieldMetadataMapping> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(FieldMetadataMapping metadata) {
        getMetadata().add(metadata);
    }

    public FieldMapping update(FieldDto field) {
        this.setDisplayName(field.getBasic().getDisplayName());
        this.setName(field.getBasic().getName());
        this.setRequired(field.getBasic().isRequired());
        this.setTooltip(field.getBasic().getTooltip());

        if (field.getBasic().getDefaultValue() != null) {
            this.setDefaultValue(field.getBasic().getDefaultValue().toString());
        }

        for (Iterator<FieldMetadataMapping> it = getMetadata().iterator(); it.hasNext();) {
            FieldMetadataMapping metadataMapping = it.next();

            boolean inNewList = false;
            for (MetadataDto metadataDto : field.getMetadata()) {
                if (Objects.equals(metadataMapping.getId(), metadataDto.getId())) {
                    inNewList = true;
                    break;
                }
            }

            if (!inNewList) {
                it.remove();
            }
        }


        for (MetadataDto metadataDto : field.getMetadata()) {
            FieldMetadataMapping metadataMapping = getMetadataById(metadataDto.getId());
            if (metadataMapping == null) {
                FieldMetadataMapping newMetadata = new FieldMetadataMapping(metadataDto);
                addMetadata(newMetadata);
            } else {
                metadataMapping.update(metadataDto);
            }
        }


        if (field.getValidation() != null) {
            for (ValidationCriterionDto criterionDto : field.getValidation().getCriteria()) {
                ValidationCriterionMapping criterion = validation.getCriterionByName(criterionDto.getDisplayName());

                criterion.setEnabled(criterionDto.isEnabled());
                criterion.setValue(criterionDto.valueAsString());
            }
        }

        return this;
    }

    public FieldMetadataMapping getMetadataById(Long metadataId) {
        for (FieldMetadataMapping metadataMapping : getMetadata()) {
            if (Objects.equals(metadataId, metadataMapping.getId())) {
                return metadataMapping;
            }
        }
        return null;
    }

    @NotPersistent
    public FieldMapping copy() {
        FieldMapping copy = new FieldMapping();

        copy.setName(name);
        copy.setDefaultValue(defaultValue);
        copy.setDisplayName(displayName);
        copy.setRequired(required);
        copy.setTooltip(tooltip);
        copy.setType(type);
        copy.setValidation((validation == null) ? null : validation.copy());
        List<FieldMetadataMapping> copyMetadata = new ArrayList<>();

        for (FieldMetadataMapping metadataMapping : metadata) {
            copyMetadata.add(metadataMapping.copy());
        }

        copy.setMetadata(copyMetadata);

        return copy;
    }
}
