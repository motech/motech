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
    private Long id;
    private String displayName;
    private String description;
    private String defaultName;
    private String typeClass;

    /**
     * TODO: Remove all the static fields after they are no longer used in temporary class "ExampleData"
     */

    /**
     * Constant <code>LONG</code> presents a long type.
     */
    public static final TypeDto LONG = new TypeDto(
            "mds.field.long", "mds.field.description.long", "longValue", Long.class.getName()
    );

    /**
     * Constant <code>INTEGER</code> presents a integer type.
     */
    public static final TypeDto INTEGER = new TypeDto(
            "mds.field.integer", "mds.field.description.integer", "integer", Integer.class.getName()
    );

    /**
     * Constant <code>STRING</code> presents a string type.
     */
    public static final TypeDto STRING = new TypeDto(
            "mds.field.string", "mds.field.description.string", "str", String.class.getName()
    );

    /**
     * Constant <code>BOOLEAN</code> presents a boolean type.
     */
    public static final TypeDto BOOLEAN = new TypeDto(
            "mds.field.boolean", "mds.field.description.boolean", "bool", Boolean.class.getName()
    );

    /**
     * Constant <code>DATE</code> presents a date type.
     */
    public static final TypeDto DATE = new TypeDto(
            "mds.field.date", "mds.field.description.date", "date", Date.class.getName()
    );

    /**
     * Constant <code>TIME</code> presents a time type.
     */
    public static final TypeDto TIME = new TypeDto(
            "mds.field.time", "mds.field.description.time", "time", Time.class.getName()
    );

    /**
     * Constant <code>DATETIME</code> presents a datetime type.
     */
    public static final TypeDto DATETIME = new TypeDto(
            "mds.field.datetime", "mds.field.description.datetime", "datetime", DateTime.class.getName()
    );

    /**
     * Constant <code>DOUBLE</code> presents a double/decimal type.
     */
    public static final TypeDto DOUBLE = new TypeDto(
            "mds.field.decimal", "mds.field.description.decimal", "dec", Double.class.getName()
    );

    /**
     * Constant <code>LIST</code> presents a list/combobox type.
     */
    public static final TypeDto LIST = new TypeDto(
            "mds.field.combobox", "mds.field.description.combobox", "list", List.class.getName()
    );

    public TypeDto() {
        this(null, null, null, null);
    }

    public TypeDto(String displayName, String description, String defaultName, String typeClass) {
        this(null, displayName, description, defaultName, typeClass);
    }

    public TypeDto(Long id, String displayName, String description, String defaultName,
                   String typeClass) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.defaultName = defaultName;
        this.typeClass = typeClass;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return EqualsBuilder.reflectionEquals(this, obj, new String[]{"id"});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
