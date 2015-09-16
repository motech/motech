package org.motechproject.mds.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.TypeHelper;
import org.motechproject.mds.util.ValidationUtil;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.motechproject.mds.util.Constants.Util.TRUE;

/**
 * The <code>Field</code> class contains information about a single field.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = TRUE)
@Unique(name = "ENTITY_FIELDNAME_IDX", members = { "entity", "name" })
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
    private String placeholder;

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

    @Persistent
    private boolean readOnly;

    @Persistent
    private boolean nonEditable;

    @Persistent
    private boolean nonDisplayable;

    @Persistent
    private boolean uiChanged;

    @Persistent(mappedBy = "field")
    @Element(dependent = "true")
    private List<FieldMetadata> metadata = new ArrayList<>();

    @Persistent(mappedBy = "field")
    @Element(dependent = "true")
    private List<FieldValidation> validations = new ArrayList<>();

    @Persistent(mappedBy = "field")
    @Element(dependent = "TRUE")
    private List<FieldSetting> settings = new ArrayList<>();

    @Persistent(mappedBy = "fields")
    private Set<Lookup> lookups = new HashSet<>();

    public Field() {
        this(null, null, null);
    }

    public Field(Entity entity, String name, String displayName) {
        this(entity, name, displayName, false, false, false, false, null, null, null, null);
    }

    public Field(Entity entity, String name, String displayName, Type type) {
        this(entity, name, displayName, type, false, false);
    }

    public Field(Entity entity, String name, String displayName, Set<Lookup> lookups) {
        this(entity, name, displayName, false, false, false, false, null, null, null, lookups);
    }

    public Field(Entity entity, String name, String displayName, Type type, boolean required, boolean readOnly) {
        this(entity, name, displayName, required, readOnly, false, false, null, null, null, null);
        this.type = type;
    }

    public Field(Entity entity, String name, String displayName, boolean required,
                 boolean readOnly, boolean nonEditable, boolean nonDisplayable, String defaultValue, String tooltip, String placeholder, Set<Lookup> lookups) {
        this(entity, name, displayName, required, readOnly, nonEditable, nonDisplayable, false, defaultValue, tooltip,
                placeholder, lookups);
    }

    public Field(Entity entity, String name, String displayName, boolean required,
                 boolean readOnly, boolean nonEditable, boolean nonDisplayable, boolean uiChanged, String defaultValue,
                 String tooltip, String placeholder, Set<Lookup> lookups) {
        this.entity = entity;
        this.displayName = displayName;
        setName(name);
        this.required = required;
        this.readOnly = readOnly;
        this.nonEditable = nonEditable;
        this.nonDisplayable = nonDisplayable;
        this.uiChanged = uiChanged;
        this.defaultValue = defaultValue;
        this.tooltip = tooltip;
        this.placeholder = placeholder;
        this.lookups = null != lookups
                ? lookups
                : new HashSet<Lookup>();
        this.exposedViaRest = true;
    }

    public List<SettingDto> settingsToDto() {
        List<SettingDto> settingsDto = new ArrayList<>();
        for (FieldSetting setting : settings) {
                settingsDto.add(setting.toDto());
        }
        return settingsDto;
    }

    public FieldDto toDto() {
        FieldBasicDto basic = new FieldBasicDto(displayName, name, required, parseDefaultValue(), tooltip, placeholder);
        TypeDto typeDto = null;

        List<MetadataDto> metaDto = new ArrayList<>();
        for (FieldMetadata meta : metadata) {
            metaDto.add(meta.toDto());
        }

        FieldValidationDto validationDto = null;

        if (CollectionUtils.isNotEmpty(validations)) {
            validationDto = new FieldValidationDto();
            for (FieldValidation validation : validations) {
                validationDto.addCriterion(validation.toDto());
            }
        }

        List<SettingDto> settingsDto = new ArrayList<>();


        for (FieldSetting setting : settings) {
            // since textArea setting is used only to distinguish between TextArea and String fields we don't display it on UI
            if (setting.getDetails().getName().equalsIgnoreCase("mds.form.label.textarea")) {
                typeDto = generateTypeForTextArea(setting);
            } else {
                settingsDto.add(setting.toDto());
            }
        }


        List<LookupDto> lookupDtos = new ArrayList<>();
        for (Lookup lookup : getLookups()) {
            lookupDtos.add(lookup.toDto());
        }

        if (typeDto == null && type != null) {
            typeDto = type.toDto();
        }

        return new FieldDto(id, entity == null ? null : entity.getId(), typeDto, basic, readOnly, nonEditable,
                nonDisplayable, uiChanged, metaDto, validationDto, settingsDto, lookupDtos);
    }

    private TypeDto generateTypeForTextArea(FieldSetting setting) {
        if (setting.getValue().equalsIgnoreCase("false")) {
            return null;
        }
        TypeDto typeDto = new TypeDto();
        typeDto.setDefaultName("textArea");
        typeDto.setDisplayName("mds.field.textArea");
        typeDto.setDescription("mds.field.description.textArea");
        typeDto.setTypeClass("textArea");
        return typeDto;
    }

    private Object parseDefaultValue() {
        Object val = this.defaultValue;

        if (StringUtils.isNotBlank((String) val)) {
            if (this.type.isCombobox()) {
                ComboboxHolder comboboxHolder = new ComboboxHolder(this);
                if (comboboxHolder.isAllowMultipleSelections()) {
                    val = TypeHelper.parse(val, List.class);
                }
            }
        }
        return val;
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

    public final void setName(String name) {
        ValidationUtil.validateNoJavaKeyword(name);
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

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
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

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
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

    public Set<Lookup> getLookups() {
        return lookups;
    }

    public void setLookups(Set<Lookup> lookups) {
        this.lookups = lookups;
    }

    public void addMetadata(FieldMetadata metadata) {
        this.metadata.add(metadata);
    }

    public FieldMetadata getMetadata(String key) {
        for (FieldMetadata meta : metadata) {
            if (StringUtils.equals(key, meta.getKey())) {
                return meta;
            }
        }
        return null;
    }

    public boolean hasMetadata(String key) {
        return null != getMetadata(key);
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

        copy.setEntity(entity);
        copy.setName(name);
        copy.setDefaultValue(defaultValue);
        copy.setDisplayName(displayName);
        copy.setRequired(required);
        copy.setTooltip(tooltip);
        copy.setPlaceholder(placeholder);
        copy.setType(type);
        copy.setReadOnly(readOnly);
        copy.setNonEditable(nonEditable);
        copy.setNonDisplayable(nonDisplayable);
        copy.setUiChanged(uiChanged);
        copy.setExposedViaRest(exposedViaRest);
        copy.setUIDisplayable(uiDisplayable);
        copy.setUIDisplayPosition(uiDisplayPosition);
        copy.setUIFilterable(uiFilterable);

        copy.setValidations(validationsCopy);
        copy.setMetadata(metadataCopy);
        copy.setSettings(settingsCopy);

        return copy;
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

    public Field update(FieldDto field) {
        setDisplayName(field.getBasic().getDisplayName());
        setName(field.getBasic().getName());
        setRequired(field.getBasic().isRequired());
        setTooltip(field.getBasic().getTooltip());
        setPlaceholder(field.getBasic().getPlaceholder());
        setReadOnly(field.isReadOnly());
        setNonEditable(field.isNonEditable());
        setNonDisplayable(field.isNonDisplayable());
        setUiChanged(field.isUiChanged());

        if (field.getBasic().getDefaultValue() != null) {
            this.setDefaultValue(field.getBasic().getDefaultValue().toString());
        }

        updateMetadata(field.getMetadata());
        updateValidation(field.getValidation());
        updateSettings(field.getSettings());

        return this;
    }

    public boolean isMultiSelectCombobox() {
        return getSettingByName(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS).getValue().equals(Constants.Util.TRUE);
    }

    private void updateMetadata(List<MetadataDto> metadataList) {
        Iterator<FieldMetadata> it = getMetadata().iterator();

        while (it.hasNext()) {
            FieldMetadata meta = it.next();
            boolean inNewList = false;

            for (MetadataDto metadataDto : metadataList) {
                if (StringUtils.equals(metadataDto.getKey(), meta.getKey())) {
                    inNewList = true;
                    break;
                }
            }

            if (!inNewList) {
                it.remove();
            }
        }

        for (MetadataDto metadataDto : metadataList) {
            FieldMetadata meta = getMetadata(metadataDto.getKey());

            if (null == meta) {
                FieldMetadata newMetadata = new FieldMetadata(metadataDto);
                addMetadata(newMetadata);
            } else {
                meta.update(metadataDto);
            }
        }
    }

    @NotPersistent
    public boolean isAutoGenerated() {
        FieldMetadata autoGenerated = getMetadata(Constants.Util.AUTO_GENERATED);
        if (null == autoGenerated) {
            autoGenerated = getMetadata(Constants.Util.AUTO_GENERATED_EDITABLE);
        }
        return null != autoGenerated && Constants.Util.TRUE.equals(autoGenerated.getValue());
    }

    public String getMetadataValue(String key) {
        FieldMetadata md = getMetadata(key);
        return md == null ? null : md.getValue();
    }

    public void setMetadataValue(String key, String value) {
        FieldMetadata md = getMetadata(key);
        if (md != null) {
            md.setValue(value);
        }
    }

    @NotPersistent
    public boolean isVersionField() {
        String metadataValue = getMetadataValue(Constants.MetadataKeys.VERSION_FIELD);
        if (StringUtils.isNotBlank(metadataValue)) {
            return new Boolean(metadataValue);
        }

        return false;
    }

    private void updateSettings(List<SettingDto> settingsList) {
        if (settingsList != null) {
            for (SettingDto settingDto : settingsList) {
                FieldSetting setting = getSettingByName(settingDto.getName());

                if (setting != null) {
                    Object value = settingDto.getValue();
                    setting.setValue(TypeHelper.format(value));
                }
            }
        }
    }

    private void updateValidation(FieldValidationDto validationDto) {
        if (null != validationDto) {
            for (ValidationCriterionDto criterionDto : validationDto.getCriteria()) {
                FieldValidation validation = getValidationByName(criterionDto.getDisplayName());

                if (null != validation) {
                    validation.setEnabled(criterionDto.isEnabled());
                    validation.setValue(criterionDto.valueAsString());
                }
            }
        }
    }

    public boolean isUiChanged() {
        return uiChanged;
    }

    public void setUiChanged(boolean uiChanged) {
        this.uiChanged = uiChanged;
    }
}
