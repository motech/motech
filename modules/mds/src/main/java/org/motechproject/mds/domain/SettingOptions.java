package org.motechproject.mds.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * The <code>SettingOptionsMapping</code> contains single setting option for given {@link TypeSettings}. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class SettingOptions {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String name;

    @Persistent
    private TypeSettings typeSettings;

    public SettingOptions(org.motechproject.mds.dto.SettingOptions option) {
        this(option.name());
    }

    public SettingOptions(String name) {
        this.name = name;
    }

    public org.motechproject.mds.dto.SettingOptions toDto() {
        return org.motechproject.mds.dto.SettingOptions.valueOf(name);
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

    public TypeSettings getTypeSettings() {
        return typeSettings;
    }

    public void setTypeSettings(TypeSettings typeSettings) {
        this.typeSettings = typeSettings;
    }

    public SettingOptions copy() {
        return new SettingOptions(name);
    }
}
