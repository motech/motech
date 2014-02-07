package org.motechproject.mds.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
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
    private boolean tracked;

    @Persistent
    private boolean exposedViaRest;

    @Persistent
    private AvailableFieldTypeMapping type;

    @Persistent(mappedBy = "field")
    @Element(dependent = "true")
    private List<FieldMetadataMapping> metadata;

    @Persistent(dependent = "TRUE")
    private TypeValidationMapping validation;

    @Persistent(mappedBy = "field")
    @Element(dependent = "TRUE")
    private List<TypeSettingsMapping> typeSettings;

    public FieldMapping() {
        metadata = new ArrayList<>();
        typeSettings = new ArrayList<>();
    }

    public FieldMapping(FieldDto field, EntityMapping entity, AvailableFieldTypeMapping type, TypeValidationMapping validation,
                        List<TypeSettingsMapping> typeSettings) {
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

        metadata = new ArrayList<>();

        for (MetadataDto meta : field.getMetadata()) {
            metadata.add(new FieldMetadataMapping(this, meta.getKey(), meta.getValue()));
        }

        this.typeSettings = null != typeSettings
                ? typeSettings
                : new ArrayList<TypeSettingsMapping>();
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

        List<SettingDto> settingDtos = new ArrayList<>();
        for (TypeSettingsMapping settings : getTypeSettings()) {
            settingDtos.add(settings.toDto());
        }

        return new FieldDto(id, entity.getId(), basicType, fieldBasic, metadataDto, validationDto, settingDtos);
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

    public String getDefaultValue() {
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
        return metadata;
    }

    public void setMetadata(List<FieldMetadataMapping> metadata) {
        this.metadata = null != metadata
                ? metadata
                : new ArrayList<FieldMetadataMapping>();
    }

    public void addMetadata(FieldMetadataMapping metadata) {
        getMetadata().add(metadata);
    }

    public List<TypeSettingsMapping> getTypeSettings() {
        return typeSettings;
    }

    public void setTypeSettings(List<TypeSettingsMapping> typeSettings) {
        this.typeSettings = null != typeSettings
                ? typeSettings
                : new ArrayList<TypeSettingsMapping>();
    }

    public FieldMapping update(FieldDto field) {
        this.setDisplayName(field.getBasic().getDisplayName());
        this.setName(field.getBasic().getName());
        this.setRequired(field.getBasic().isRequired());
        this.setTooltip(field.getBasic().getTooltip());

        if (field.getBasic().getDefaultValue() != null) {
            this.setDefaultValue(field.getBasic().getDefaultValue().toString());
        }

        updateMetadata(field.getMetadata());
        updateValidation(field.getValidation());
        updateSettings(field.getSettings());

        return this;
    }

    public void updateMetadata(List<MetadataDto> metadataList) {
        for (Iterator<FieldMetadataMapping> it = getMetadata().iterator(); it.hasNext();) {
            FieldMetadataMapping metadataMapping = it.next();

            boolean inNewList = false;
            for (MetadataDto metadataDto : metadataList) {
                if (Objects.equals(metadataMapping.getId(), metadataDto.getId())) {
                    inNewList = true;
                    break;
                }
            }

            if (!inNewList) {
                it.remove();
            }
        }

        for (MetadataDto metadataDto : metadataList) {
            FieldMetadataMapping metadataMapping = getMetadataById(metadataDto.getId());
            if (metadataMapping == null) {
                FieldMetadataMapping newMetadata = new FieldMetadataMapping(metadataDto);
                addMetadata(newMetadata);
            } else {
                metadataMapping.update(metadataDto);
            }
        }
    }

    public void updateSettings(List<SettingDto> settingsList) {
        for (SettingDto settingDto : settingsList) {
            TypeSettingsMapping settings = getTypeSettingsByName(settingDto.getName());
            if (settings != null) {
                settings.setValue(settings.getValueType().format(settingDto.getValue()));
            }
        }
    }

    public void updateValidation(FieldValidationDto validationDto) {
        if (validationDto != null) {
            for (ValidationCriterionDto criterionDto : validationDto.getCriteria()) {
                ValidationCriterionMapping criterion = validation.getCriterionByName(criterionDto.getDisplayName());

                criterion.setEnabled(criterionDto.isEnabled());
                criterion.setValue(criterionDto.valueAsString());
            }
        }
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
        copy.setTracked(tracked);
        copy.setExposedViaRest(exposedViaRest);

        copy.setValidation((validation == null) ? null : validation.copy());

        List<FieldMetadataMapping> copyMetadata = new ArrayList<>();
        for (FieldMetadataMapping metadataMapping : metadata) {
            copyMetadata.add(metadataMapping.copy());
        }
        copy.setMetadata(copyMetadata);

        List<TypeSettingsMapping> typeSettingsCopy = new ArrayList<>();
        for (TypeSettingsMapping typeSettingsInstance : getTypeSettings()) {
            typeSettingsCopy.add(typeSettingsInstance.copy());
        }
        copy.setTypeSettings(typeSettingsCopy);

        return copy;
    }

    public TypeSettingsMapping getTypeSettingsByName(String name) {
        for (TypeSettingsMapping settings : getTypeSettings()) {
            if (StringUtils.equals(name, settings.getName())) {
                return settings;
            }
        }
        return null;
    }

    public boolean isTracked() {
        return tracked;
    }

    public void setTracked(boolean tracked) {
        this.tracked = tracked;
    }

    public boolean isExposedViaRest() {
        return exposedViaRest;
    }

    public void setExposedViaRest(boolean exposedViaRest) {
        this.exposedViaRest = exposedViaRest;
    }

}
