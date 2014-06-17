package org.motechproject.mds.query;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.util.InstanceSecurityRestriction;

import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.motechproject.mds.util.SecurityUtil.getUsername;

/**
 * The <code>QueryExecutor</code> util class provides methods that help execute a JDO query.
 */
public final class QueryExecutor {

    private QueryExecutor() {
    }

    public static Object execute(Query query, InstanceSecurityRestriction restriction) {
        if (restriction != null && !restriction.isEmpty()) {
            return query.execute(getUsername());
        } else {
            return query.execute();
        }
    }

    public static Object execute(Query query, Object value, InstanceSecurityRestriction restriction) {
        return executeWithArray(query, new Object[] {value}, restriction);
    }

    public static Object executeWithArray(Query query, Object[] values,
                                          InstanceSecurityRestriction restriction) {
        // We unwrap ranges into two objects
        Object[] unwrappedValues = unwrap(values);

        if (restriction != null && !restriction.isEmpty() && unwrappedValues.length > 0) {
            return query.executeWithArray(unwrappedValues, getUsername());
        } else if (restriction != null && !restriction.isEmpty()) {
            return query.executeWithArray(getUsername());
        } else {
            return query.executeWithArray(unwrappedValues);
        }
    }

    public static Object executeWithArray(Query query, List<Property> properties) {
        // We unwrap ranges into two objects
        Object[] unwrappedValues = unwrap(properties.toArray());

        return query.executeWithArray(unwrappedValues);
    }

    public static Object executeWithFilter(Query query, Filter filter,
                                           InstanceSecurityRestriction restriction) {
        return executeWithArray(query, filter.valuesForQuery(), restriction);
    }

    private static Object[] unwrap(Object[] values) {
        List<Object> unwrapped = new ArrayList<>();

        if (ArrayUtils.isNotEmpty(values)) {
            for (Object object : values) {
                if (object instanceof Range) {
                    Range range = (Range) object;
                    unwrapRange(unwrapped, range);
                } else if (object instanceof Set) {
                    Set set = (Set) object;
                    unwrapSet(unwrapped, set);
                } else if (object instanceof Property) {
                    Property property = (Property) object;
                    unwrapProperty(unwrapped, property);
                } else if (object != null) { // we skip null values
                    unwrapped.add(object);
                }
            }
        }

        return unwrapped.toArray();
    }

    private static void unwrapSet(Collection unwrappedCol, Set set) {
        for (Object o : set) {
            if (o != null) {
                unwrappedCol.add(o);
            }
        }
    }

    private static void unwrapRange(Collection unwrappedCol, Range range) {
        if (range.getMin() != null || range.getMax() != null) {
            unwrappedCol.add(range.getMin());
            unwrappedCol.add(range.getMax());
        }
    }

    private static void unwrapProperty(Collection unwrappedCol, Property property) {
        Collection unwrap = property.unwrap();

        if (null != unwrap) {
            unwrappedCol.addAll(unwrap);
        }
    }
}
