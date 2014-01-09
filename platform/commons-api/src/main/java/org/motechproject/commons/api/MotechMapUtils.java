package org.motechproject.commons.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The <code>MotechMapUtils</code> class contains methods that allow modifications and operations on maps
 */
public final class MotechMapUtils {

    private MotechMapUtils() {

    }

    /**
     * Null-safe merge of two maps. If both parameters are null it returns empty map.
     * If one of the maps is null, it returns the other one. If a key is present in two maps,
     * the value in the merged map will be taken from the overriding map.
     * @param overridingMap The map overriding values in the base map
     * @param baseMap The base map
     * @return merged map
     */
    public static Map<Object, Object> mergeMaps(Map<Object,Object> overridingMap, Map<Object, Object> baseMap) {
        Map<Object,Object> mergedMap = new LinkedHashMap<>();

        if (baseMap==null && overridingMap!=null) {
            mergedMap.putAll(overridingMap);
        } else if (overridingMap==null && baseMap!=null) {
            mergedMap.putAll(baseMap);
        } else if (overridingMap!=null && baseMap!=null) {
            mergedMap.putAll(baseMap);
            mergedMap.putAll(overridingMap);
        }
        return mergedMap;
    }

    /**
     * Converts java.util.Map into java.util.Properties
     * @param map Map to convert
     * @return Properties, created from given map
     */
    public static Properties asProperties(Map<Object, Object> map) {
        Properties properties = new Properties();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

        return properties;
    }

}
