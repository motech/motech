package org.motechproject.mds.rest;

import org.motechproject.mds.util.PropertyUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The <code>RestProjection</code> class represents entity fields projection onto entity fields exposed
 * over REST. Because of its Map interface inheritance, we can leave output handling to other components.
 * Additionally, LinkedHashMap ensures unchanged fields order.
 *
 * @see org.motechproject.mds.rest.MdsRestFacade
 */
public class RestProjection extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 4867207873054128121L;

    public static <T> List<RestProjection> createProjectionCollection(Collection<T> collection, List<String> fields) {
        List<RestProjection> projectionCollection = new ArrayList<>(collection.size());
        for (T element : collection) {
            projectionCollection.add(createProjection(element, fields));
        }
        return projectionCollection;
    }

    public static <T> RestProjection createProjection(T element, List<String> fields) {
        RestProjection projection = new RestProjection();
        for (String field : fields) {
            Object value = PropertyUtil.safeGetProperty(element, field);
            projection.put(field, value);
        }
        return projection;
    }
}
