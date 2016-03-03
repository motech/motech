package org.motechproject.mds.filter;

import org.motechproject.mds.dto.FieldDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents multiple filters for multiple fields. Responsible for collecting and joining queries, parameters and values.
 */
public class Filters {

    private List<Filter> filters;

    public Filters(Filter[] filters) {
        this.filters = Arrays.asList(filters);
    }

    public Filters(Filter filter) {
        this.filters = new ArrayList(Arrays.asList(filter));
    }

    public Object[] valuesForQuery() {
        if (requiresFiltering()) {
            ArrayList<Object> values = new ArrayList<>();
            for (Filter filter : filters) {
                values.addAll(Arrays.asList(filter.valuesForQuery()));
            }
            return values.toArray();
        } else {
            return new Object[0];
        }
    }

    public boolean requiresFiltering() {
        if (filters != null) {
             for (Filter filter : filters) {
                 if (filter.requiresFiltering()) {
                     return true;
                 }
             }
            return false;
        }
        return false;
    }

    public void setMultiselect(List<FieldDto> fields) {
        for (FieldDto field : fields) {
            if (field.multiSelect()) {
                Filter filter = filterForField(field);
                if (filter != null) {
                    filter.setMultiSelect();
                }
            }
        }
    }

    public String filterForQuery() {
        StringBuilder query = new StringBuilder();
        int i = 0;
        int j;
        for (Filter filter : filters) {
            if (i != 0) {
                query.append(" && ");
            }
            List<List <String>> queryFilters = filter.filterForQueryAsList();
            if (queryFilters.size() > 1) {
                query.append("(");
            }
            j = 0;
            for (List<String> queryFilter : queryFilters) {
                if (j++ != 0) {
                    query.append(" || ");
                }
                query.append(queryFilter.get(0));
                query.append("arg");
                query.append(Integer.toString(i++));
                if (queryFilter.size() > 1) {
                    query.append(queryFilter.get(1));
                }
            }
            if (queryFilters.size() > 1) {
                query.append(")");
            }
        }
        return query.toString();
    }

    public String paramsDeclarationForQuery() {
        StringBuilder paramsForQuery = new StringBuilder();
        int i = 0;
        for (Filter filter : filters) {
            for (String type : filter.paramsDeclarationForQueryAsList()) {
                paramsForQuery.append(type);
                paramsForQuery.append("arg");
                paramsForQuery.append(Integer.toString(i++));
                paramsForQuery.append(",");
            }
        }
        paramsForQuery.setLength(paramsForQuery.length() - 1);
        return paramsForQuery.toString();
    }

    private Filter filterForField(FieldDto field) {
        String name = field.getBasic().getName();
        for (Filter filter : filters) {
            if (filter.getField().equals(name)) {
                return filter;
            }
        }
        return null;
    }
}
