package org.motechproject.mds.domain;

import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.SettingOptions;
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
 * The <code>TypeSettingsMapping</code> contains settings for given {@link org.motechproject.mds.domain.AvailableFieldTypeMapping}. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class TypeSettingsMapping {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String name;

    @Persistent
    private String value;

    @Persistent
    private AvailableFieldTypeMapping valueType;

    @Persistent(mappedBy = "typeSettings")
    @Element(dependent = "true")
    private List<SettingOptionsMapping> settingOptions;

    @Persistent
    private AvailableFieldTypeMapping type;

    @Persistent
    private FieldMapping field;

    public TypeSettingsMapping(String name, String value, AvailableFieldTypeMapping valueType, AvailableFieldTypeMapping type, SettingOptionsMapping... options) {
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
        List<SettingOptions> options = new ArrayList<>();
        for (SettingOptionsMapping settingOption : settingOptions) {
            options.add(settingOption.toDto());
        }
        return new SettingDto(name, valueType.parse(value), new TypeDto(valueType.getDisplayName(), valueType.getDescription(), valueType.getTypeClass()),
                options.toArray(new SettingOptions[options.size()]));
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

    public AvailableFieldTypeMapping getType() {
        return type;
    }

    public void setType(AvailableFieldTypeMapping type) {
        this.type = type;
    }

    public AvailableFieldTypeMapping getValueType() {
        return valueType;
    }

    public void setValueType(AvailableFieldTypeMapping valueType) {
        this.valueType = valueType;
    }

    public FieldMapping getField() {
        return field;
    }

    public void setField(FieldMapping field) {
        this.field = field;
    }

    public List<SettingOptionsMapping> getSettingOptions() {
        if (settingOptions == null) {
            settingOptions = new ArrayList<>();
        }
        return settingOptions;
    }

    public void setSettingOptions(List<SettingOptionsMapping> settingOptions) {
        this.settingOptions = settingOptions;
    }

    public TypeSettingsMapping copy() {
        List<SettingOptionsMapping> settingsOptionsCopy = new ArrayList<>();

        for (SettingOptionsMapping settingOption : getSettingOptions()) {
            settingsOptionsCopy.add(settingOption.copy());
        }

        return new TypeSettingsMapping(name, value, valueType, type,
                settingsOptionsCopy.toArray(new SettingOptionsMapping[settingsOptionsCopy.size()]));
    }
}
