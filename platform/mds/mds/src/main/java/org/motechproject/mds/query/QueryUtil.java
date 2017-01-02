package org.motechproject.mds.query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.util.InstanceSecurityRestriction;

import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.motechproject.mds.util.SecurityUtil.getUsername;

/**
 * The <code>QueryUtil</code> util class provides methods that help developer to create a JDO
 * query.
 *
 * @see javax.jdo.Query
 */
public final class QueryUtil {

    private static final String QUERY_CANNOT_BE_NULL = "Query cannot be null";

    private QueryUtil() {
    }

    public static void setQueryParams(Query query, QueryParams queryParams) {
        if (query == null) {
            throw new IllegalArgumentException("Cannot set parameters for a null query");
        }

        if (queryParams != null) {
            if (queryParams.isPagingSet()) {
                long page = queryParams.getPage();
                long pageSize = queryParams.getPageSize();

                // these are 0 based
                long fromIncl = page * pageSize - pageSize;
                long toExcl = page * pageSize;

                query.setRange(fromIncl, toExcl);
            }
            if (queryParams.isOrderSet()) {
                String order = StringUtils.join(queryParams.getOrderList(), ", ");
                query.setOrdering(order);
            }
        }
    }

    public static void useFilters(Query query, Filters filters) {
        if (query == null) {
            throw new IllegalArgumentException(QUERY_CANNOT_BE_NULL);
        }

        if (filters != null && filters.requiresFiltering()) {
            query.setFilter(filters.filterForQuery());
            query.declareParameters(filters.paramsDeclarationForQuery());
        }
    }

    public static void useFilter(Query query, String[] properties, Object[] values, Map<String, String> fieldTypeMap) {
        useFilter(query, properties, values, fieldTypeMap, null);
    }

    public static void useFilter(Query query, String[] properties, Object[] values, Map<String, String> fieldTypeMap,
                                 InstanceSecurityRestriction restriction) {
        if (properties.length != values.length) {
            throw new IllegalArgumentException("properties length must equal to values length");
        }

        List<Property> list = new ArrayList<>();

        for (int i = 0; i < properties.length; i++) {
            String prop = properties[i];
            Object value = values[i];

            String type = getFieldType(prop, fieldTypeMap, value);

            // skip if we cannot determine type
            if (type != null) {
                list.add(PropertyBuilder.create(prop, values[i], type));
            }
        }

        useFilter(query, list, restriction);
    }

    public static void useFilter(Query query, List<Property> properties) {
        useFilter(query, properties, null);
    }

    public static void useFilter(Query query, List<Property> properties,
                                 InstanceSecurityRestriction restriction) {
        if (query == null) {
            throw new IllegalArgumentException(QUERY_CANNOT_BE_NULL);
        }

        List<Property> copy = new ArrayList<>(properties);

        if (restriction != null && !restriction.isEmpty()) {
            copy.add(new RestrictionProperty(restriction, getUsername()));
        }

        Collection<CharSequence> filters = new ArrayList<>(copy.size());
        Collection<CharSequence> declareParameter = new ArrayList<>(copy.size());

        for (int idx = 0; idx < copy.size(); ++idx) {
            Property property = copy.get(idx);

            CollectionUtils.addIgnoreNull(filters, property.asFilter(idx));
            CollectionUtils.addIgnoreNull(declareParameter, property.asDeclareParameter(idx));
        }

        query.setFilter(StringUtils.join(filters, " && "));
        query.declareParameters(StringUtils.join(declareParameter, ", "));
    }

    public static void useFilterFromPattern(Query query, String pattern, List<Property> properties) {
        int propertiesCount = (properties == null) ? 0 : properties.size();

        CharSequence[] propertyArgs = new CharSequence[propertiesCount];
        CharSequence[] declareParameters = new CharSequence[propertiesCount];

        for (int i = 0; i < propertiesCount; i++) {
            propertyArgs[i] = properties.get(i).asFilter(i);
            declareParameters[i] = properties.get(i).asDeclareParameter(i);
        }

        query.setFilter(String.format(pattern, propertyArgs));
        query.declareParameters(StringUtils.join(declareParameters, ", "));
    }

    /**
     * Returns the string in a form a pattern used for searching with the matches operator.
     * @param string The string to search for.
     * @return The string in format {@code .*<string>.*} or the string unchanged if it is empty.
     */
    public static String asMatchesPattern(String string) {
        return StringUtils.isNotEmpty(string) ? String.format(".*%s.*", string) : string;
    }

    public static String asEqualsPattern(String string) {
        return StringUtils.defaultString(string);
    }

    public static void setCountResult(Query query) {
        if (query == null) {
            throw new IllegalArgumentException(QUERY_CANNOT_BE_NULL);
        }
        query.setResult("count(this)");
    }

    private static String getFieldType(String property, Map<String, String> fieldTypeMap, Object value) {
        String type = null;

        if (fieldTypeMap != null) {
            type = fieldTypeMap.get(property);
        } else if (value != null) {
            type = value.getClass().getName();
        }

        return type;
    }
}
