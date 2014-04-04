package org.motechproject.mds.util;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.filter.Filter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.jdo.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The <code>QueryUtil</code> util class provides methods that help developer to create a JDO
 * query.
 *
 * @see javax.jdo.Query
 */
public final class QueryUtil {
    public static final String PARAM_PREFIX = "param";
    public static final String FILTER_AND = " && ";
    public static final String FILTER_OR = " || ";
    public static final String DECLARE_PARAMETERS_COMMA = ", ";
    public static final String EQUALS_SIGN = "==";
    public static final String SPACE = " ";
    public static final String GREATER_EQUAL = ">=";
    public static final String LESS_EQUAL = "<=";
    public static final String LOWER_BOUND_SUFIX = "lb";
    public static final String UPPER_BOUND_SUFIX = "ub";

    private QueryUtil() {
    }

    public static String createFilter(String property) {
        return createFilter(new String[]{property}, null, null);
    }

    /**
     * Creates the string representation of filters that should be added to JDO query. This method
     * create a single filter using the following pattern:
     * <p/>
     * <strong>${property}==param${number}</strong>
     * <p/>
     * where:
     * <ul>
     * <li><strong>${property}</strong> is a property from the passed array,</li>
     * <li><strong>${number}</strong> is a index of the property (started from zero).</li>
     * </ul>
     * <p/>
     * If the passed array contains more that one element then the filters are connected with the
     * logical <i>and</i> (&&).
     *
     * @param properties the array of properties that should be placed in filter.
     * @return the string representation of the query filter.
     * @see #createFilter(String[])
     */
    public static String createFilter(String[] properties) {
        return createFilter(properties, null, null);
    }

    public static String createFilter(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        StringBuilder filter = new StringBuilder();

        for (int i = 0; i < properties.length; ++i) {
            String property = properties[i];

            if (0 != i) {
                filter.append(FILTER_AND);
            }

            if (values != null && values[i] instanceof Range) {
                buildFilterForRange(filter, property, i);
            } else if (values != null && values[i] instanceof Set) {
                Set set = (Set) values[i];
                buildFilterForSet(filter, set, property, i);
            } else {
                filter.append(property);
                filter.append(EQUALS_SIGN);
                filter.append(PARAM_PREFIX).append(i);
            }
        }

        // append a restriction either by user or by creator
        if (restriction != null && !restriction.isEmpty()) {
            buildFilterForRestriction(filter, restriction, properties.length);
        }

        return filter.toString();
    }

    private static void buildFilterForSet(StringBuilder filter, Set set, String property, int paramIndex) {
        filter.append('(');

        for (int i = 0; i < set.size(); i++) {
            filter.append(property);
            filter.append(EQUALS_SIGN);
            filter.append(PARAM_PREFIX).append(paramIndex).append('_').append(i);

            if (i < set.size() - 1) {
                filter.append(FILTER_OR);
            }
        }

        filter.append(')');
    }

    private static void buildFilterForRange(StringBuilder filter, String property, int paramIndex) {
        filter.append(property);
        filter.append(GREATER_EQUAL);
        filter.append(PARAM_PREFIX).append(paramIndex).append(LOWER_BOUND_SUFIX);

        filter.append(FILTER_AND);

        filter.append(property);
        filter.append(LESS_EQUAL);
        filter.append(PARAM_PREFIX).append(paramIndex).append(UPPER_BOUND_SUFIX);
    }

    private static void buildFilterForRestriction(StringBuilder filter, InstanceSecurityRestriction restriction,
                                                  int propertiesLength) {
        if (propertiesLength > 0) {
            filter.append(FILTER_AND);
        }

        if (restriction.isByCreator()) {
            filter.append("creator");
        } else if (restriction.isByOwner()) {
            filter.append("owner");
        }

        filter.append(EQUALS_SIGN);
        filter.append(PARAM_PREFIX);
        filter.append(propertiesLength);
    }

    public static String createDeclareParameters(Object value) {
        return createDeclareParameters(new Object[]{value}, null);
    }

    /**
     * Creates the string representation of declare parameters that should be added to JDO query.
     * This method create a single parameter declaration using the following pattern:
     * <p/>
     * <strong>${valueClassName} param${number}</strong>
     * <p/>
     * where:
     * <ul>
     * <li><strong>${valueClassName}</strong> is a value class name from the passed array,</li>
     * <li><strong>${number}</strong> is a index of the value (started from zero).</li>
     * </ul>
     * <p/>
     * If the passed array contains more that one element then the parameter declarations are
     * connected with the <i>comma</i>.
     *
     * @param values the array of properties that should be placed in parameters declaration.
     * @return the string representation of the query parameters declaration.
     * @see #createFilter(String[])
     */
    public static String createDeclareParameters(Object[] values) {
        return createDeclareParameters(values, null);
    }

    public static String createDeclareParameters(Object[] values, InstanceSecurityRestriction restriction) {
        StringBuilder parameters = new StringBuilder();

        for (int i = 0; i < values.length; ++i) {
            Object value = values[i];

            if (0 != i) {
                parameters.append(DECLARE_PARAMETERS_COMMA);
            }

            if (value instanceof Range) {
                Range range = (Range) value;
                String typeClass = getTypeOfRange(range);

                parameters.append(typeClass);
                parameters.append(SPACE);
                parameters.append(PARAM_PREFIX).append(i).append(LOWER_BOUND_SUFIX);

                parameters.append(DECLARE_PARAMETERS_COMMA);

                parameters.append(typeClass);
                parameters.append(SPACE);
                parameters.append(PARAM_PREFIX).append(i).append(UPPER_BOUND_SUFIX);
            } else if (value instanceof Set) {
                Set set = (Set) value;
                String typeClass = getTypeOfSet(set);

                for (int j = 0; j < set.size(); j++) {
                    parameters.append(typeClass);
                    parameters.append(SPACE);
                    parameters.append(PARAM_PREFIX).append(i).append('_').append(j);

                    if (j < set.size() - 1) {
                        parameters.append(DECLARE_PARAMETERS_COMMA);
                    }
                }
            } else {
                parameters.append(value.getClass().getName());
                parameters.append(SPACE);
                parameters.append(PARAM_PREFIX).append(i);
            }
        }

        // append a restriction either by user or by creator
        if (restriction != null && !restriction.isEmpty()) {
            if (values.length > 0) {
                parameters.append(DECLARE_PARAMETERS_COMMA);
            }

            parameters.append(String.class.getName());
            parameters.append(SPACE);

            parameters.append(PARAM_PREFIX);
            parameters.append(values.length);
        }

        return parameters.toString();
    }

    public static void setQueryParams(Query query, QueryParams queryParams) {
        if (query == null) {
            throw new IllegalArgumentException("Cannot set parameters for a null query");
        }

        if (queryParams != null) {
            if (queryParams.isPagingSet()) {
                long page = queryParams.getPage();
                long pageSize = queryParams.getPageSize();

                long fromIncl = page * pageSize - pageSize;
                long toExcl = page * pageSize + 1;

                query.setRange(fromIncl, toExcl);
            }
            if (queryParams.isOrderSet()) {
                query.setOrdering(queryParams.getOrder().toString());
            }
        }
    }

    public static void useFilter(Query query, Filter filter) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }
        if (filter != null && filter.requiresFiltering()) {
            query.setFilter(filter.filterForQuery());
            query.declareParameters(filter.paramsDeclarationForQuery());
        }
    }

    public static Object execute(Query query, InstanceSecurityRestriction restriction) {
        if (restriction != null && !restriction.isEmpty()) {
            return query.execute(getUsername());
        } else {
            return query.execute();
        }
    }

    public static Object executeWithArray(Query query, Object[] values, InstanceSecurityRestriction restriction) {
        // We unwrap ranges into two objects
        Object[] unwrappedValues = unwrap(values);
        if (restriction != null && !restriction.isEmpty()) {
            return query.executeWithArray(unwrappedValues, getUsername());
        } else {
            return query.executeWithArray(unwrappedValues);
        }
    }

    public static Object executeWithFilter(Query query, Filter filter, InstanceSecurityRestriction restriction) {
        return executeWithArray(query, filter.valuesForQuery(), restriction);
    }

    private static String getUsername() {
        String username = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            username = authentication.getName();
        }

        return username;
    }

    private static Object[] unwrap(Object... values) {
        List<Object> unwrapped = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(values)) {
            for (Object object : values) {
                if (object instanceof Range) {
                    Range range = (Range) object;
                    unwrapped.add(range.getMin());
                    unwrapped.add(range.getMax());
                } else if (object instanceof Set) {
                    Set set = (Set) object;
                    unwrapped.addAll(set);
                } else {
                    unwrapped.add(object);
                }
            }
        }
        return unwrapped.toArray(new Object[unwrapped.size()]);
    }

    private static String getTypeOfRange(Range range) {
        Object val = range.getMin();
        if (val == null) {
            val = range.getMax();
        }
        if (val == null) {
            throw new IllegalArgumentException("Empty range provided for query");
        }

        return val.getClass().getName();
    }

    private static String getTypeOfSet(Set set) {
        Object val = null;
        if (set != null && !set.isEmpty()) {
            val = set.iterator().next();
        }

        if (val == null) {
            throw new IllegalArgumentException("Empty set provided for query");
        }

        return val.getClass().getName();
    }
}
