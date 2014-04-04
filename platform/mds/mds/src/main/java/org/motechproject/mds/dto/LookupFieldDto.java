package org.motechproject.mds.dto;

import java.util.Objects;

/**
 * Represents a field added to a lookup. The lookup using a given field can be done using multiple lookup types.
 */
public class LookupFieldDto {

    private Long id;
    private String name;
    private Type type;

    public LookupFieldDto() {
    }

    public LookupFieldDto(Long id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LookupFieldDto)) {
            return false;
        }

        LookupFieldDto that = (LookupFieldDto) o;

        return Objects.equals(that.getId(), getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * The lookup type represents whether the lookup will be done by comparing to a single field,
     * matching values to a range, or matching to a set of values.
     */
    public static enum Type {
        VALUE, RANGE, SET
    }
}
