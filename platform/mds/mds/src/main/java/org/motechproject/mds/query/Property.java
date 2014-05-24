package org.motechproject.mds.query;

import java.util.Arrays;
import java.util.Collection;

/**
 * The <code>Property</code> class represent a property that will be used in JDO query. Classes that
 * extend this class should define how that property should be used in WHERE section in JDO query.
 *
 * @param <T> type of the passed value
 */
public abstract class Property<T> {
    private String name;
    private T value;

    protected Property(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public CharSequence asFilter(int idx) {
        return null == getValue() ? null : generateFilter(idx);
    }

    protected abstract CharSequence generateFilter(int idx);

    public CharSequence asDeclareParameter(int idx) {
        return null == getValue() ? null : generateDeclareParameter(idx);
    }

    protected CharSequence generateDeclareParameter(int idx) {
        return String.format("%s param%d", getValue().getClass().getName(), idx);
    }

    public Collection unwrap() {
        return null == getValue() ? null : Arrays.asList(getValue());
    }
}
