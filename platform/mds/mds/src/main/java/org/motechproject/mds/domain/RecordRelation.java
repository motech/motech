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

    @Persistent
    private Long objectId;

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

    public RecordRelation() {
    }

    public RecordRelation(Long objectId) {
        this.objectId = objectId;
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

    private static RecordRelation fromItem(Object item) {
        return (item == null) ? null :
                new RecordRelation((Long) PropertyUtil.safeGetProperty(item, Constants.Util.ID_FIELD_NAME));
    }
}
