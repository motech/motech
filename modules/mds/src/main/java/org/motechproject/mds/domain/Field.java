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
public class Field {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private Entity entity;

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
    private AvailableFieldType type;

    @Persistent
    private boolean uiDisplayable;

    @Persistent
    private Long uiDisplayPosition;

    @Persistent
    private boolean uiFilterable;

    @Persistent(mappedBy = "field")
    @Element(dependent = "true")
    private List<FieldMetadata> metadata;

    @Persistent(dependent = "TRUE")
    private TypeValidation validation;

    @Persistent(mappedBy = "field")
    @Element(dependent = "TRUE")
    private List<TypeSettings> typeSettings;

    public Field() {
        metadata = new ArrayList<>();
        typeSettings = new ArrayList<>();
    }

    public Field(FieldDto field, Entity entity, AvailableFieldType type, TypeValidation validation,
                 List<TypeSettings> typeSettings) {
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
            metadata.add(new FieldMetadata(this, meta.getKey(), meta.getValue()));
        }

        this.typeSettings = null != typeSettings
                ? typeSettings
                : new ArrayList<TypeSettings>();
    }

    public FieldDto toDto() {
        FieldBasicDto fieldBasic = new FieldBasicDto(displayName, name, required, defaultValue, tooltip);
        List<MetadataDto> metadataDto = new ArrayList<>();

        if (null != metadata) {
            for (FieldMetadata meta : metadata) {
                metadataDto.add(meta.toDto());
            }
        }

        TypeDto basicType = new TypeDto(type.toDto().getType().getDisplayName(), type.toDto().getType().getDisplayName(), type.toDto().getType().getTypeClass());
        FieldValidationDto validationDto = (validation == null) ? null : validation.toDto();

        List<SettingDto> settingDtos = new ArrayList<>();
        for (TypeSettings settings : getTypeSettings()) {
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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
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

    public AvailableFieldType getType() {
        return type;
    }

    public void setType(AvailableFieldType type) {
        this.type = type;
    }

    public TypeValidation getValidation() {
        return validation;
    }

    public void setValidation(TypeValidation validation) {
        this.validation = validation;
    }

    public List<FieldMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<FieldMetadata> metadata) {
        this.metadata = null != metadata
                ? metadata
                : new ArrayList<FieldMetadata>();
    }

    public void addMetadata(FieldMetadata metadata) {
        getMetadata().add(metadata);
    }

    public List<TypeSettings> getTypeSettings() {
        return typeSettings;
    }

    public void setTypeSettings(List<TypeSettings> typeSettings) {
        this.typeSettings = null != typeSettings
                ? typeSettings
                : new ArrayList<TypeSettings>();
    }

    public Field update(FieldDto field) {
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
        for (Iterator<FieldMetadata> it = getMetadata().iterator(); it.hasNext(); ) {
            FieldMetadata metadataMapping = it.next();

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
            FieldMetadata metadataMapping = getMetadataById(metadataDto.getId());
            if (metadataMapping == null) {
                FieldMetadata newMetadata = new FieldMetadata(metadataDto);
                addMetadata(newMetadata);
            } else {
                metadataMapping.update(metadataDto);
            }
        }
    }

    public void updateSettings(List<SettingDto> settingsList) {
        for (SettingDto settingDto : settingsList) {
            TypeSettings settings = getTypeSettingsByName(settingDto.getName());
            if (settings != null) {
                settings.setValue(settings.getValueType().format(settingDto.getValue()));
            }
        }
    }

    public void updateValidation(FieldValidationDto validationDto) {
        if (validationDto != null) {
            for (ValidationCriterionDto criterionDto : validationDto.getCriteria()) {
                ValidationCriterion criterion = validation.getCriterionByName(criterionDto.getDisplayName());

                criterion.setEnabled(criterionDto.isEnabled());
                criterion.setValue(criterionDto.valueAsString());
            }
        }
    }

    public FieldMetadata getMetadataById(Long metadataId) {
        for (FieldMetadata metadataMapping : getMetadata()) {
            if (Objects.equals(metadataId, metadataMapping.getId())) {
                return metadataMapping;
            }
        }
        return null;
    }

    @NotPersistent
    public Field copy() {
        Field copy = new Field();

        copy.setName(name);
        copy.setDefaultValue(defaultValue);
        copy.setDisplayName(displayName);
        copy.setRequired(required);
        copy.setTooltip(tooltip);
        copy.setType(type);
        copy.setTracked(tracked);
        copy.setExposedViaRest(exposedViaRest);
        copy.setUIDisplayable(uiDisplayable);
        copy.setUIDisplayPosition(uiDisplayPosition);
        copy.setUIFilterable(uiFilterable);

        copy.setValidation((validation == null) ? null : validation.copy());

        List<FieldMetadata> copyMetadata = new ArrayList<>();
        for (FieldMetadata metadataMapping : metadata) {
            copyMetadata.add(metadataMapping.copy());
        }
        copy.setMetadata(copyMetadata);

        List<TypeSettings> typeSettingsCopy = new ArrayList<>();
        for (TypeSettings typeSettingsInstance : getTypeSettings()) {
            typeSettingsCopy.add(typeSettingsInstance.copy());
        }
        copy.setTypeSettings(typeSettingsCopy);

        return copy;
    }

    public TypeSettings getTypeSettingsByName(String name) {
        for (TypeSettings settings : getTypeSettings()) {
            if (StringUtils.equals(name, settings.getName())) {
                return settings;
            }
        }
        return null;
    }

    public boolean isUIDisplayable() {
        return uiDisplayable;
    }

    public void setUIDisplayable(boolean uiDisplayable) {
        this.uiDisplayable = uiDisplayable;
    }

    public Long getUIDisplayPosition() {
        return uiDisplayPosition;
    }

    public void setUIDisplayPosition(Long uiDisplayPosition) {
        this.uiDisplayPosition = uiDisplayPosition;
    }

    public boolean isUIFilterable() {
        return uiFilterable;
    }

    public void setUIFilterable(boolean uiFilterable) {
        this.uiFilterable = uiFilterable;
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
