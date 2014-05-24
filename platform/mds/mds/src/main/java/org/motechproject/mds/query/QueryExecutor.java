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

    public static Object executeWithArray(Query query, List<Property> properties,
                                          InstanceSecurityRestriction restriction) {
        // We unwrap ranges into two objects
        Object[] unwrappedValues = unwrap(properties.toArray());

        if (restriction != null && !restriction.isEmpty() && unwrappedValues.length > 0) {
            return query.executeWithArray(unwrappedValues, getUsername());
        } else if (restriction != null && !restriction.isEmpty()) {
            return query.executeWithArray(getUsername());
        } else {
            return query.executeWithArray(unwrappedValues);
        }
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
                    unwrapped.add(range.getMin());
                    unwrapped.add(range.getMax());
                } else if (object instanceof Set) {
                    Set set = (Set) object;
                    unwrapped.addAll(set);
                } else if (object instanceof Property) {
                    Property property = (Property) object;
                    Collection unwrap = property.unwrap();

                    if (null != unwrap) {
                        unwrapped.addAll(unwrap);
                    }
                } else if (object != null) { // we skip null values
                    unwrapped.add(object);
                }
            }
        }

        return unwrapped.toArray();
    }

}
