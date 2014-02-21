package org.motechproject.mds.jdo;

import org.datanucleus.store.types.converters.TypeConverter;
import org.motechproject.commons.date.model.Time;

/**
 * This is datanucleus type converter we plug in. It is responsible for converting
 * {@link org.motechproject.commons.date.model.Time} instances to Strings which are persisted.
 */
public class TimeTypeConverter implements TypeConverter<Time, String> {

    private static final long serialVersionUID = 4777820253513893758L;

    @Override
    public String toDatastoreType(Time memberValue) {
        return memberValue.timeStr();
    }

    @Override
    public Time toMemberType(String datastoreValue) {
        return Time.valueOf(datastoreValue);
    }
}
