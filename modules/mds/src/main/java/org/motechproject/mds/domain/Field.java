package org.motechproject.mds.domain;

import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
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
 * The <code>Field</code> class contains information about a single field.
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
    private Type type;

    @Persistent
    private boolean uiDisplayable;

    @Persistent
    private Long uiDisplayPosition;

    @Persistent
    private boolean uiFilterable;

    @Persistent(mappedBy = "field")
    @Element(dependent = "true")
    private List<FieldMetadata> metadata = new ArrayList<>();

    @Persistent(mappedBy = "field")
    @Element(dependent = "true")
    private List<FieldValidation> validations = new ArrayList<>();

    @Persistent(mappedBy = "field")
    @Element(dependent = "TRUE")
    private List<FieldSetting> settings = new ArrayList<>();

    public Field() {
        this(null, null, null);
    }

    public Field(Entity entity, String displayName, String name) {
        this(entity, displayName, name, false, null, null);
    }

    public Field(Entity entity, String displayName, String name, boolean required,
                 String defaultValue, String tooltip) {
        this.entity = entity;
        this.displayName = displayName;
        this.name = name;
        this.required = required;
        this.defaultValue = defaultValue;
        this.tooltip = tooltip;
    }

    public FieldDto toDto() {
        FieldBasicDto basic = new FieldBasicDto(displayName, name, required, defaultValue, tooltip);

        List<MetadataDto> metaDto = new ArrayList<>();
        for (FieldMetadata meta : metadata) {
            metaDto.add(meta.toDto());
        }

        FieldValidationDto valiDto = new FieldValidationDto();
        for (FieldValidation validation : validations) {
            valiDto.addCriterion(validation.toDto());
        }

        List<SettingDto> settingDto = new ArrayList<>();
        for (FieldSetting setting : settings) {
            settingDto.add(setting.toDto());
        }

        return new FieldDto(id, entity.getId(), type.toDto(), basic, metaDto, valiDto, settingDto);
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<FieldValidation> getValidations() {
        return validations;
    }

    public void setValidations(List<FieldValidation> validations) {
        this.validations = validations;
    }

    public void addValidation(FieldValidation validation) {
        this.validations.add(validation);
    }

    public FieldValidation getValidationByName(String name) {
        FieldValidation found = null;

        for (FieldValidation validation : validations) {
            if (validation.getDetails().getDisplayName().equalsIgnoreCase(name)) {
                found = validation;
                break;
            }
        }

        return found;
    }

    public List<FieldMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<FieldMetadata> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(FieldMetadata metadata) {
        this.metadata.add(metadata);
    }

    public FieldMetadata getMetadataById(Long id) {
        FieldMetadata found = null;

        for (FieldMetadata meta : metadata) {
            if (meta.getId().equals(id)) {
                found = meta;
                break;
            }
        }

        return found;
    }

    public List<FieldSetting> getSettings() {
        return settings;
    }

    public void setSettings(List<FieldSetting> settings) {
        this.settings = settings;
    }

    public void addSetting(FieldSetting setting) {
        this.settings.add(setting);
    }

    public FieldSetting getSettingByName(String name) {
        FieldSetting found = null;

        for (FieldSetting setting : settings) {
            if (setting.getDetails().getName().equalsIgnoreCase(name)) {
                found = setting;
                break;
            }
        }

        return found;
    }

    @NotPersistent
    public Field copy() {
        Field copy = new Field();

        List<FieldMetadata> metadataCopy = new ArrayList<>();
        for (FieldMetadata meta : metadata) {
            metadataCopy.add(meta.copy());
        }

        List<FieldSetting> settingsCopy = new ArrayList<>();
        for (FieldSetting setting : settings) {
            FieldSetting settingCopy = setting.copy();
            settingCopy.setField(copy);

            settingsCopy.add(settingCopy);
        }

        List<FieldValidation> validationsCopy = new ArrayList<>();
        for (FieldValidation validation : validations) {
            FieldValidation validationCopy = validation.copy();
            validationCopy.setField(copy);

            validationsCopy.add(validationCopy);
        }

        copy.setName(name);
        copy.setDefaultValue(defaultValue);
        copy.setDisplayName(displayName);
        copy.setRequired(required);
        copy.setTooltip(tooltip);
        copy.setType(type);
        copy.setTracked(tracked);
        copy.setExposedViaRest(exposedViaRest);
        copy.setValidations(validationsCopy);
        copy.setMetadata(metadataCopy);
        copy.setSettings(settingsCopy);

        return copy;
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

    public Field update(FieldDto field) {
        setDisplayName(field.getBasic().getDisplayName());
        setName(field.getBasic().getName());
        setRequired(field.getBasic().isRequired());
        setTooltip(field.getBasic().getTooltip());

        if (field.getBasic().getDefaultValue() != null) {
            this.setDefaultValue(field.getBasic().getDefaultValue().toString());
        }

        updateMetadata(field.getMetadata());
        updateValidation(field.getValidation());
        updateSettings(field.getSettings());

        return this;
    }

    private void updateMetadata(List<MetadataDto> metadataList) {
        Iterator<FieldMetadata> it = getMetadata().iterator();

        while (it.hasNext()) {
            FieldMetadata meta = it.next();
            boolean inNewList = false;

            for (MetadataDto metadataDto : metadataList) {
                if (Objects.equals(meta.getId(), metadataDto.getId())) {
                    inNewList = true;
                    break;
                }
            }

            if (!inNewList) {
                it.remove();
            }
        }

        for (MetadataDto metadataDto : metadataList) {
            FieldMetadata meta = getMetadataById(metadataDto.getId());

            if (null == meta) {
                FieldMetadata newMetadata = new FieldMetadata(metadataDto);
                addMetadata(newMetadata);
            } else {
                meta.update(metadataDto);
            }
        }
    }

    private void updateSettings(List<SettingDto> settingsList) {
        for (SettingDto settingDto : settingsList) {
            FieldSetting setting = getSettingByName(settingDto.getName());

            if (setting != null) {
                Type valueType = setting.getDetails().getValueType();
                Object value = settingDto.getValue();
                setting.setValue(valueType.format(value));
            }
        }
    }

    private void updateValidation(FieldValidationDto validationDto) {
        if (validationDto != null) {
            for (ValidationCriterionDto criterionDto : validationDto.getCriteria()) {
                FieldValidation validation = getValidationByName(criterionDto.getDisplayName());

                validation.setEnabled(criterionDto.isEnabled());
                validation.setValue(criterionDto.valueAsString());
            }
        }
    }
}
