package org.motechproject.mds.helper;


import org.motechproject.mds.domain.Field;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.motechproject.mds.util.Constants.Settings.ALLOW_MULTIPLE_SELECTIONS;

/**
 * Helper class for listing selection type changes.
 */
public final class ComboboxHelper {

    /**
     * Utility class, should not be instantiated.
     */
    private ComboboxHelper() {

    }

    /**
     * Returns map of fields with changed selection type. Key is fields name and value defines whether field will be
     * using multi-select or not.
     *
     * @param oldFields  the definitions of the fields before change
     * @param newFields  the definitions of the fields after change
     * @return  the map of fields with changed selection type
     */
    public static Map<String, Boolean> comboboxesWithChangedSelectionType(List<Field> oldFields, List<Field> newFields) {

        Map<String, Boolean> oldFieldsMap = listToMap(oldFields);
        Map<String, Boolean> newFieldsMap = listToMap(newFields);

        for (Iterator<Map.Entry<String, Boolean>> i = newFieldsMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<String, Boolean> entry = i.next();
            if (!oldFieldsMap.containsKey(entry.getKey()) || oldFieldsMap.entrySet().contains(entry)) {
                i.remove();
            }
        }

        return newFieldsMap;
    }

    private static Map<String, Boolean> listToMap(List<Field> fields) {
        Map<String, Boolean> map = new HashMap<>();
        for (Field field : fields) {
            map.put(field.getName(), Boolean.valueOf(field.getSettingByName(ALLOW_MULTIPLE_SELECTIONS).getValue()));
        }
        return map;
    }
}
