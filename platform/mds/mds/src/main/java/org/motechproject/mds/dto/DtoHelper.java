package org.motechproject.mds.dto;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for managing dto collections.
 */
public final class DtoHelper {

    /**
     * Stores fields in a map using id as the key for faster lookup
     * @param fields the field collection
     * @return a map with field ids being the keys and fields being the values
     */
    public static Map<Long, FieldDto> asFieldMapById(Collection<FieldDto> fields) {
        Map<Long, FieldDto> fieldMap = new HashMap<>();
        for (FieldDto field : fields) {
            fieldMap.put(field.getId(), field);
        }
        return fieldMap;
    }

    /**
     * Stores fields in a map using name as the key for faster lookup
     * @param fields the field collection
     * @return a map with field names being the keys and fields being the values
     */
    public static Map<String, FieldDto> asFieldMapByName(Collection<FieldDto> fields) {
        Map<String, FieldDto> fieldMap = new HashMap<>();
        for (FieldDto field : fields) {
            fieldMap.put(field.getBasic().getName(), field);
        }
        return fieldMap;
    }

    public static FieldDto findByName(Collection<FieldDto> fields, String name) {
        for (FieldDto field : fields) {
            if (StringUtils.equals(name, field.getBasic().getName())) {
                return field;
            }
        }
        return null;
    }

    public static FieldDto findById(Collection<FieldDto> fields, Long id) {
        for (FieldDto field : fields) {
            if (Objects.equals(id, field.getId())) {
                return field;
            }
        }
        return null;
    }

    private DtoHelper() {
    }
}
