package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The <code>ValidationCriterionDto</code> contains information about single criterion for
 * field validation.
 */
public class ValidationCriterionDto {
    private String displayName;
    private TypeDto type;
    private Object value;
    private boolean enabled;

    public ValidationCriterionDto() {
        this(null, null);
    }

    public ValidationCriterionDto(String displayName, TypeDto type) {
        this(displayName, type, "", false);
    }

    public ValidationCriterionDto(String displayName, TypeDto type, Object value, boolean enabled) {
        this.displayName = displayName;
        this.type = type;
        this.value = value;
        this.enabled = enabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public TypeDto getType() {
        return type;
    }

    public void setType(TypeDto type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
