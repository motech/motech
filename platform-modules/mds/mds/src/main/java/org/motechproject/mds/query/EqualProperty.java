package org.motechproject.mds.query;

import org.motechproject.mds.util.LookupName;

/**
 * The <code>EqualProperty</code> class represents a property that will be used in JDO query
 * and it has to be equal to the given value.
 *
 * @param <T> type of the passed value
 */
public class EqualProperty<T> extends Property<T> {

    public EqualProperty(String name, T value, String type) {
        super(name, value, type);
    }

    public EqualProperty(String jdoVariableName, String name, T value, String type) {
        super(jdoVariableName, name, value, type);
    }

    @Override
    public CharSequence generateFilter(int idx) {
        if (isForRelation()) {
            return String.format("%s.contains(%s) && %s.%s == param%d", LookupName.getFieldName(getName()), getJdoVariableName(), getJdoVariableName(),
                    LookupName.getRelatedFieldName(getName()), idx);

        }
        return String.format("%s == param%d", getName(), idx);
    }
}
