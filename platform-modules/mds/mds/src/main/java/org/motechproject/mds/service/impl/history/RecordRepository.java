package org.motechproject.mds.service.impl.history;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for storing records persisted in history during a single TX.
 * The purpose is updating records in case one record gets multiple store events, which may
 * happen in case of relationship trees.
 */
class RecordRepository {

    // key in the first map is the history class
    // key in the second map is the id of the actual object
    // the value is the historical record
    private Map<String, Map<Long, Object>> recordMap = new HashMap<>();

    void store(Long id, Object object) {
        String className = object.getClass().getName();
        getMapForEntityClass(className).put(id, object);
    }

    Object get(String className, Long id) {
        return getMapForEntityClass(className).get(id);
    }

    boolean contains(String className, Long id) {
        return getMapForEntityClass(className).containsKey(id);
    }

    void clear() {
        recordMap.clear();
    }

    private Map<Long, Object> getMapForEntityClass(String className) {
        Map<Long, Object> mapForEntity = recordMap.get(className);
        if (mapForEntity == null) {
            mapForEntity = new HashMap<>();
            recordMap.put(className, mapForEntity);
        }
        return mapForEntity;
    }
}
