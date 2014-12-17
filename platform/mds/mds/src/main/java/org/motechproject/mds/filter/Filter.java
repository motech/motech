package org.motechproject.mds.filter;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a filter on a field.
 */
public class Filter implements Serializable {

    private static final long serialVersionUID = 20316350610920063L;

    private String field;
    private List<FilterValue> values;

    public Filter() {
    }

    public Filter(String field, FilterValue[] values) {
        this.field = field;
        this.values = Arrays.asList(values);
    }

    public Filter(String field, String value) {
        this.field = field;
        this.values = new ArrayList<>(Arrays.asList(FilterValue.fromString(value)));
    }

    public void setMultiSelect() {
        if (values.get(0) instanceof ComboboxFilterValue) {
            for (FilterValue value : values) {
                ((ComboboxFilterValue) value).setMultiSelect();
            }
        }
    }

    public String getField() {
        return field;
    }

    public List<FilterValue> getValues() {
        return values;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setValues(FilterValue[] type) {
        this.values = Arrays.asList(type);
    }

    public boolean requiresFiltering() {
        return values != null ? values.size() != 0 : false;
    }

    public String filterForQuery() {
        return StringUtils.join(filterForQueryAsList(), " || ");
    }

    public List<List <String>> filterForQueryAsList() {
        List<List <String>> filters = new ArrayList<>();
        for (FilterValue value : values) {
            if (value.operatorForQueryFilter().size() > 1) {
                List<String> subList = new ArrayList<>();
                subList.add(String.format("%s%s", field, value.operatorForQueryFilter().get(0)));
                subList.add(value.operatorForQueryFilter().get(1));
                filters.add(subList);
            } else {
                filters.add(Arrays.asList(String.format("%s %s ", field, value.operatorForQueryFilter().get(0))));
            }
        }
        return filters;
    }

    public String paramsDeclarationForQuery() {
        return StringUtils.join(paramsDeclarationForQueryAsList(), ",");
    }

    public List<String> paramsDeclarationForQueryAsList() {
        List<String> params = new ArrayList<>();
        for (FilterValue value : values) {
            params.add(String.format("%s ", value.paramTypeForQuery()));
        }
        return params;
    }

    public Object[] valuesForQuery() {
        if (requiresFiltering()) {
            List<Object> valuesList = new ArrayList<>();
            for (FilterValue value : this.values) {
                valuesList.add(value.valueForQuery());
            }
            return valuesList.toArray();
        } else {
            return new Object[0];
        }
    }
}
