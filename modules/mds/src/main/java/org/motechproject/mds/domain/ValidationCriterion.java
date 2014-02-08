package org.motechproject.mds.domain;

import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * The <code>ValidationCriterionMapping</code> class represents single validation criterion. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class ValidationCriterion {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String displayName;

    @Persistent
    private TypeValidation validation;

    @Persistent
    private AvailableFieldType type;

    @Persistent
    private String value;

    @Persistent
    private boolean enabled;

    public ValidationCriterion(String displayName, TypeValidation validation, AvailableFieldType type) {
        this(displayName, "", false, validation, type);
    }

    public ValidationCriterion(String displayName, String value, boolean enabled, TypeValidation validation, AvailableFieldType type) {
        this.displayName = displayName;
        this.value = value;
        this.enabled = enabled;
        this.validation = validation;
        this.type = type;
    }

    public ValidationCriterionDto toDto() {
        return new ValidationCriterionDto(displayName, new TypeDto(type.getDisplayName(), type.getDescription(), type.getTypeClass()),
                type.parse(value), enabled);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AvailableFieldType getType() {
        return type;
    }

    public void setType(AvailableFieldType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TypeValidation getValidation() {
        return validation;
    }

    public void setValidation(TypeValidation validation) {
        this.validation = validation;
    }

    public ValidationCriterion copy() {
        return new ValidationCriterion(displayName, value, enabled, null, type);
    }
}
