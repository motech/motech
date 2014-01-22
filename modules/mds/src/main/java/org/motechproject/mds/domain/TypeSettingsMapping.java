package org.motechproject.mds.domain;

import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.SettingOptions;
import org.motechproject.mds.dto.TypeDto;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The <code>TypeSettingsMapping</code> contains settings for given {@link org.motechproject.mds.domain.AvailableFieldTypeMapping}. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class TypeSettingsMapping {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String name;

    @Persistent
    private String value;

    @Column(name = "valueType")
    private AvailableFieldTypeMapping valueType;

    @Element(column = "settingId", dependent = "true")
    private Set<SettingOptionsMapping> settingOptions;

    @Column(name = "type")
    private AvailableFieldTypeMapping type;

    public TypeSettingsMapping(String name, String value, AvailableFieldTypeMapping valueType, AvailableFieldTypeMapping type, SettingOptionsMapping... options) {
        this.name = name;
        this.value = value;
        this.valueType = valueType;
        this.type = type;
        if (options != null) {
            settingOptions = new HashSet<>();
            for (SettingOptionsMapping settingOption : options) {
                settingOptions.add(settingOption);
            }
        }
    }

    public SettingDto toDto() {
        List<SettingOptions> options = new ArrayList<>();
        for (SettingOptionsMapping settingOption : settingOptions) {
            options.add(settingOption.toDto());
        }
        return new SettingDto(name, value, new TypeDto(valueType.getDisplayName(), valueType.getDescription(), valueType.getTypeClass()), options.toArray(new SettingOptions[options.size()]));
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

    public Set<SettingOptionsMapping> getSettingOptions() {
        return settingOptions;
    }

    public void setSettingOptions(Set<SettingOptionsMapping> settingOptions) {
        this.settingOptions = settingOptions;
    }
}
