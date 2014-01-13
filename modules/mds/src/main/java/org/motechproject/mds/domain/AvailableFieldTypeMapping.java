package org.motechproject.mds.domain;

import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.TypeDto;

import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Unique;

/**
 * The <code>FieldTypeMapping</code> class is a representation of database records
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class AvailableFieldTypeMapping {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private String id;

    @Persistent
    private String defaultName;

    @Persistent
    @Unique
    private String displayName;

    @Persistent
    private String description;

    @Persistent
    private String typeClass;

    public AvailableFieldTypeMapping() {
        this(null, null, null);
    }

    public AvailableFieldTypeMapping(String id, String defaultName, TypeDto type) {
        this.id = id;
        this.defaultName = defaultName;
        this.displayName = type == null ? null : type.getDisplayName();
        this.description = type == null ? null : type.getDescription();
        this.typeClass = type == null ? null : type.getTypeClass();
    }

    public AvailableTypeDto toDto() {
        return new AvailableTypeDto(this.getId(), this.getDefaultName(), new TypeDto(displayName, description, typeClass));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
