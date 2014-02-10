package org.motechproject.mds.domain;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.List;

/**
 * The <code>TypeSetting</code> contains settings for the given mds type. This class is related
 * with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class TypeSetting {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String name;

    @Persistent(table = "TYPE_SETTING_SETTING_OPTION")
    @Join(column = "TYPE_SETTING_ID_OID")
    @Element(column = "SETTING_OPTION_ID_EID")
    private List<TypeSettingOption> typeSettingOptions;

    @Column(name = "TYPE_ID")
    private Type valueType;

    private String defaultValue;

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

    public List<TypeSettingOption> getTypeSettingOptions() {
        return typeSettingOptions;
    }

    public void setTypeSettingOptions(List<TypeSettingOption> typeSettingOptions) {
        this.typeSettingOptions = typeSettingOptions;
    }

    public Type getValueType() {
        return valueType;
    }

    public void setValueType(Type valueType) {
        this.valueType = valueType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
