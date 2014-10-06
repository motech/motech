package org.motechproject.mds.domain;

import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a relationships on a trashed object.
 */
@PersistenceCapable
public class RecordRelation {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent(column = Constants.Util.OBJECT_ID_COLUMN)
    private Long objectId;

    @Persistent
    private String relatedObjectClassName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getRelatedObjectClassName() {
        return relatedObjectClassName;
    }

    public void setRelatedObjectClassName(String relatedObjectClassName) {
        this.relatedObjectClassName = relatedObjectClassName;
    }

    public RecordRelation() {
    }

    public RecordRelation(Long objectId, String relatedObjectClassName) {
        this.objectId = objectId;
        this.relatedObjectClassName = relatedObjectClassName;
    }

    public static Object fromFieldValue(Object value) {
        if (value instanceof Collection) {
            List<RecordRelation> result = new ArrayList<>();
            Collection asCollection = (Collection) value;

            for (Object item : asCollection) {
                result.add(fromItem(item));
            }

            return result;
        } else {
            return fromItem(value);
        }
    }

    /**
     * Checks whether the object is an RecordRelation object, or a collection of such objects
     * @param val the object to such
     * @return true if we this a RecordRelation object, or a collection in which the first element
     *              is a RecordRelation object, false otherwise
     */
    public static boolean isRecordRelation(Object val) {
        if (val instanceof Collection) {
            Collection asColl = (Collection) val;
            // we only check the first item, we assume this collection is of one type
            return !asColl.isEmpty() && asColl.iterator().next() instanceof RecordRelation;
        } else {
            return val instanceof RecordRelation;
        }
    }

    private static RecordRelation fromItem(Object item) {
        return (item == null) ? null :
                new RecordRelation((Long) PropertyUtil.safeGetProperty(item, Constants.Util.ID_FIELD_NAME),
                        item.getClass().getName());
    }
}
