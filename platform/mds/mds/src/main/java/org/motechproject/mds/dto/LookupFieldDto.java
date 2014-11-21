package org.motechproject.mds.dto;

import java.util.Objects;

/**
 * Represents a field added to a lookup. The lookup using a given field can be done using multiple lookup types.
 */
public class LookupFieldDto {

    private Long id;
    private String name;
    private Type type;
    private String customOperator;
    private boolean useGenericParam;

    public LookupFieldDto() {
    }

    public LookupFieldDto(String name, Type type) {
        this(null, name, type);
    }

    public LookupFieldDto(Long id, String name, Type type) {
        this(id, name, type, null);
    }

    public LookupFieldDto(Long id, String name, Type type, String customOperator) {
        this(id, name, type, customOperator, false);
    }

    public LookupFieldDto(Long id, String name, Type type, String customOperator, boolean useGenericParam) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.customOperator = customOperator;
        this.useGenericParam = useGenericParam;
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

    public String getCustomOperator() {
        return customOperator;
    }

    public void setCustomOperator(String customOperator) {
        this.customOperator = customOperator;
    }

    public boolean isUseGenericParam() {
        return useGenericParam;
    }

    public void setUseGenericParam(boolean useGenericParam) {
        this.useGenericParam = useGenericParam;
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

        return Objects.equals(that.getId(), id) && Objects.equals(that.getName(), name) &&
                Objects.equals(that.getCustomOperator(), customOperator) &&
                Objects.equals(that.getType(), type) &&
                Objects.equals(that.isUseGenericParam(), useGenericParam);
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
