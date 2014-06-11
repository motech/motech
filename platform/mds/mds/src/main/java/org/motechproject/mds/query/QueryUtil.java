package org.motechproject.mds.query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.util.InstanceSecurityRestriction;

import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.motechproject.mds.util.SecurityUtil.getUsername;

/**
 * The <code>QueryUtil</code> util class provides methods that help developer to create a JDO
 * query.
 *
 * @see javax.jdo.Query
 */
public final class QueryUtil {

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

    public static void useFilter(Query query, String[] properties, Object[] values) {
        useFilter(query, properties, values, null);
    }

    public static void useFilter(Query query, String[] properties, Object[] values,
                                 InstanceSecurityRestriction restriction) {
        if (properties.length != values.length) {
            throw new IllegalArgumentException("properties length must equal to values length");
        }

        List<Property> list = new ArrayList<>();

        for (int i = 0; i < properties.length; i++) {
            list.add(PropertyBuilder.create(properties[i], values[i]));
        }

        useFilter(query, list, restriction);
    }

    public static void useFilter(Query query, List<Property> properties) {
        useFilter(query, properties, null);
    }

    public static void useFilter(Query query, List<Property> properties,
                                 InstanceSecurityRestriction restriction) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
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
        CharSequence[] propertyArgs = new String[propertiesCount];

        for (int i = 0; i < propertiesCount; i++) {
            propertyArgs[i] = properties.get(i).asFilter(i);
        }

        query.setFilter(String.format(pattern, propertyArgs));
    }
}
