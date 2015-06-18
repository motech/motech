package org.motechproject.mds.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.motechproject.mds.util.Constants.Util.ID_FIELD_NAME;

/**
 * Represents an object reference repository. It holds historical objects for the
 * real objects with the given id and class name.
 */
public class ObjectReferenceRepository {

    private Map<Key, Object> objectsByHistoricalObject = new HashMap<>();

    private class Key {
        private Long objectId;
        private String className;

        public Key(Long objectId, String className) {
            this.objectId = objectId;
            this.className = className;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            final Key other = (Key) obj;

            return Objects.equals(this.objectId, other.objectId)
                    && Objects.equals(this.className, other.className);
        }

        @Override
        public int hashCode() {
            return Objects.hash(objectId, className);
        }
    }

    public void saveHistoricalObject(Object nonHistoricalObject, Object historicalObject) {
        Long nonHistoricalObjectId = (Long) PropertyUtil.safeGetProperty(nonHistoricalObject, ID_FIELD_NAME);
        String nonHistoricalObjectClassName = nonHistoricalObject.getClass().getName();

        objectsByHistoricalObject.put(new Key(nonHistoricalObjectId, nonHistoricalObjectClassName), historicalObject);
    }

    public Object getHistoricalObject(Object nonHistoricalObject) {
        Long objectId = (Long) PropertyUtil.safeGetProperty(nonHistoricalObject, ID_FIELD_NAME);
        String objectClass = nonHistoricalObject.getClass().getName();

        return objectsByHistoricalObject.get(new Key(objectId, objectClass));
    }
}
