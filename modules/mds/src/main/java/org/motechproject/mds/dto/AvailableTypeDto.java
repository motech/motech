package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The <code>AvailableTypeDto</code> class contains information about an available for selection
 * field.
 */
public class AvailableTypeDto {
    private String id;
    private String defaultName;
    private TypeDto type;

    public AvailableTypeDto() {
        this(null, null, null);
    }

    public AvailableTypeDto(String defaultName, TypeDto type) {
        this(null, defaultName, type);
    }

    public AvailableTypeDto(String id, String defaultName, TypeDto type) {
        this.id = id;
        this.defaultName = defaultName;
        this.type = type;
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

    public TypeDto getType() {
        return type;
    }

    public void setType(TypeDto type) {
        this.type = type;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AvailableTypeDto)) {
            return false;
        }

        AvailableTypeDto that = (AvailableTypeDto) o;

        if (defaultName != null ? !defaultName.equals(that.defaultName) : that.defaultName != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
