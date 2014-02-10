package org.motechproject.mds.domain;

import org.motechproject.mds.dto.ValidationCriterionDto;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * The <code>FieldValidation</code> class contains the value that is related with the correct
 * type validation and information about that whether the given validation is enabled or not.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class FieldValidation {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private Field field;

    @Column(name = "DETAILS_ID")
    private TypeValidation details;

    private String value;

    private boolean enabled;

    public FieldValidation() {
        this(null, null);
    }

    public FieldValidation(Field field, TypeValidation details) {
        this(field, details, null, false);
    }

    public FieldValidation(Field field, TypeValidation details, String value, boolean enabled) {
        this.field = field;
        this.details = details;
        this.value = value;
        this.enabled = enabled;
    }

    public ValidationCriterionDto toDto() {
        Type valueType = details.getValueType();

        ValidationCriterionDto dto = new ValidationCriterionDto();
        dto.setDisplayName(details.getDisplayName());
        dto.setType(details.getValueType().toDto());
        dto.setEnabled(enabled);
        dto.setValue(valueType.parse(value));

        return dto;
    }

    public FieldValidation copy() {
        FieldValidation copy = new FieldValidation();
        copy.setEnabled(enabled);
        copy.setDetails(details);
        copy.setValue(value);

        return copy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public TypeValidation getDetails() {
        return details;
    }

    public void setDetails(TypeValidation details) {
        this.details = details;
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
}
