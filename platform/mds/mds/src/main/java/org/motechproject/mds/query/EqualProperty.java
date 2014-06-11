package org.motechproject.mds.query;

/**
 * The <code>EqualProperty</code> class represents a property that will be used in JDO query
 * and it has to be equal to the given value.
 *
 * @param <T> type of the passed value
 */
public class EqualProperty<T> extends Property<T> {

    public EqualProperty(String name, T value) {
        super(name, value);
    }

    @Override
    public CharSequence generateFilter(int idx) {
        return String.format("%s == param%d", getName(), idx);
    }
}
