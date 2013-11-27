package org.motechproject.mds.web.comparator;

import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;

import java.util.Comparator;

/**
 * The <code>EntityRecordComparator</code> class compares two objects of
 * {@link EntityRecord}  by value of their field property
 */
public class EntityRecordComparator implements Comparator<EntityRecord> {

    private boolean sortAscending;
    private String compareField;

    public EntityRecordComparator(boolean sortAscending, String compareField) {
        this.sortAscending = sortAscending;
        this.compareField = compareField;
    }

    @Override
    public int compare(EntityRecord one, EntityRecord two) {
        FieldRecord fieldFromOne = findFieldByName(one, compareField);
        FieldRecord fieldFromTwo = findFieldByName(two, compareField);

        int ret = fieldFromOne.getValue().toString().compareTo(fieldFromTwo.getValue().toString());
        return (sortAscending) ? ret : -ret;
    }

    private FieldRecord findFieldByName(EntityRecord entity, String fieldName) {
        for (FieldRecord field : entity.getFields()) {
            if (field.getDisplayName().equals(fieldName)) {
                return field;
            }
        }

        return null;
    }
}
