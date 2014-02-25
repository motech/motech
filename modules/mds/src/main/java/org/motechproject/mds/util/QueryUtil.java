package org.motechproject.mds.util;

import javax.jdo.Query;

/**
 * The <code>QueryUtil</code> util class provides methods that help developer to create a JDO
 * query.
 *
 * @see javax.jdo.Query
 */
public final class QueryUtil {
    private static final String PARAM_PREFIX = "param";
    private static final String FILTER_AND = " && ";
    private static final String DECLARE_PARAMETERS_COMMA = ", ";
    private static final String EQUALS_SIGN = "==";
    private static final String SPACE = " ";

    private QueryUtil() {
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
        StringBuilder filter = new StringBuilder();

        for (int i = 0; i < properties.length; ++i) {
            String property = properties[i];

            if (0 != i) {
                filter.append(FILTER_AND);
            }

            filter.append(property);
            filter.append(EQUALS_SIGN);
            filter.append(PARAM_PREFIX);
            filter.append(i);
        }

        return filter.toString();
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
        StringBuilder parameters = new StringBuilder();

        for (int i = 0; i < values.length; ++i) {
            Object value = values[i];

            if (0 != i) {
                parameters.append(DECLARE_PARAMETERS_COMMA);
            }

            parameters.append(value.getClass().getName());
            parameters.append(SPACE);
            parameters.append(PARAM_PREFIX);
            parameters.append(i);
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
}
