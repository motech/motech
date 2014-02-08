package org.motechproject.mds.domain;

import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <code>TypeSettingsMapping</code> contains settings for given {@link AvailableFieldType}. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class TypeSettings {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String name;

    @Persistent
    private String value;

    @Persistent
    private AvailableFieldType valueType;

    @Persistent(mappedBy = "typeSettings")
    @Element(dependent = "true")
    private List<SettingOptions> settingOptions;

    @Persistent
    private AvailableFieldType type;

    @Persistent
    private Field field;

    public TypeSettings(String name, String value, AvailableFieldType valueType, AvailableFieldType type, SettingOptions... options) {
        this.name = name;
        this.value = value;
        this.valueType = valueType;
        this.type = type;
        if (options != null) {
            settingOptions = new ArrayList<>();
            Collections.addAll(settingOptions, options);
        }
    }

    public SettingDto toDto() {
        List<org.motechproject.mds.dto.SettingOptions> options = new ArrayList<>();
        for (SettingOptions settingOption : settingOptions) {
            options.add(settingOption.toDto());
        }
        return new SettingDto(name, valueType.parse(value), new TypeDto(valueType.getDisplayName(), valueType.getDescription(), valueType.getTypeClass()),
                options.toArray(new org.motechproject.mds.dto.SettingOptions[options.size()]));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AvailableFieldType getType() {
        return type;
    }

    public void setType(AvailableFieldType type) {
        this.type = type;
    }

    public AvailableFieldType getValueType() {
        return valueType;
    }

    public void setValueType(AvailableFieldType valueType) {
        this.valueType = valueType;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public List<SettingOptions> getSettingOptions() {
        if (settingOptions == null) {
            settingOptions = new ArrayList<>();
        }
        return settingOptions;
    }

    public void setSettingOptions(List<SettingOptions> settingOptions) {
        this.settingOptions = settingOptions;
    }

    public TypeSettings copy() {
        List<SettingOptions> settingsOptionsCopy = new ArrayList<>();

        for (SettingOptions settingOption : getSettingOptions()) {
            settingsOptionsCopy.add(settingOption.copy());
        }

        return new TypeSettings(name, value, valueType, type,
                settingsOptionsCopy.toArray(new SettingOptions[settingsOptionsCopy.size()]));
    }
}
