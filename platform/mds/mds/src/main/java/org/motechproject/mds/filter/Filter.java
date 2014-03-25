package org.motechproject.mds.filter;

import java.io.Serializable;

/**
 * Represents a filter on a field.
 */
public class Filter implements Serializable {

    private static final long serialVersionUID = 20316350610920063L;

    private String field;
    private FilterType type;

    public Filter() {
    }

    public Filter(String field, FilterType type) {
        this.field = field;
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public FilterType getType() {
        return type;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public boolean requiresFiltering() {
        return type != FilterType.ALL;
    }

    public String filterForQuery() {
        return String.format("%s %s arg0", field, type.operatorForQueryFilter());
    }

    public String paramsDeclarationForQuery() {
        return String.format("%s arg0", type.paramTypeForQuery());
    }

    public Object[] valuesForQuery() {
        if (requiresFiltering()) {
            return new Object[]{ type.valueForQuery() };
        } else {
            return new Object[0];
        }
    }
}
