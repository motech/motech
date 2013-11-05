package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;

import java.util.Date;
import java.util.List;

/**
 * The <code>TypeDto</code> class contains information about an available field in an entity.
 */
public class TypeDto {
    private String displayName;
    private String description;
    private String typeClass;

    /**
     * Constant <code>INTEGET</code> presents a integer type.
     */
    public static final TypeDto INTEGER = new TypeDto(
            "mds.field.integer", "mds.field.description.integer", Integer.class.getName()
    );

    /**
     * Constant <code>STRING</code> presents a string type.
     */
    public static final TypeDto STRING = new TypeDto(
            "mds.field.string", "mds.field.description.string", String.class.getName()
    );

    /**
     * Constant <code>BOOLEAN</code> presents a boolean type.
     */
    public static final TypeDto BOOLEAN = new TypeDto(
            "mds.field.boolean", "mds.field.description.boolean", Boolean.class.getName()
    );

    /**
     * Constant <code>DATE</code> presents a date type.
     */
    public static final TypeDto DATE = new TypeDto(
            "mds.field.date", "mds.field.description.date", Date.class.getName()
    );

    /**
     * Constant <code>TIME</code> presents a time type.
     */
    public static final TypeDto TIME = new TypeDto(
            "mds.field.time", "mds.field.description.time", Time.class.getName()
    );

    /**
     * Constant <code>DATETIME</code> presents a datetime type.
     */
    public static final TypeDto DATETIME = new TypeDto(
            "mds.field.datetime", "mds.field.description.datetime", DateTime.class.getName()
    );

    /**
     * Constant <code>DOUBLE</code> presents a double/decimal type.
     */
    public static final TypeDto DOUBLE = new TypeDto(
            "mds.field.decimal", "mds.field.description.decimal", Double.class.getName()
    );

    /**
     * Constant <code>LIST</code> presents a list/combobox type.
     */
    public static final TypeDto LIST = new TypeDto(
            "mds.field.combobox", "mds.field.description.combobox", List.class.getName()
    );

    public TypeDto() {
        this(null, null, null);
    }

    public TypeDto(String displayName, String description, String typeClass) {
        this.displayName = displayName;
        this.description = description;
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

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
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
