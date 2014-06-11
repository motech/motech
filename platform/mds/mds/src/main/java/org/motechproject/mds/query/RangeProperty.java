package org.motechproject.mds.query;

import org.motechproject.commons.api.Range;

import java.util.Arrays;
import java.util.Collection;

/**
 * The <code>RangeProperty</code> class represents a property that will be used in JDO query
 * and it has to be inside the given range.
 *
 * @param <T> type used in range.
 */
public class RangeProperty<T> extends Property<Range<T>> {

    public RangeProperty(String name, Range<T> value) {
        super(name, value);
    }

    @Override
    public CharSequence generateFilter(int idx) {
        return String.format("%1$s>=param%2$dlb && %1$s<=param%2$dub", getName(), idx);
    }

    @Override
    public CharSequence generateDeclareParameter(int idx) {
        String typeClass = getTypeOfRange();
        return String.format("%1$s param%2$dlb, %1$s param%2$dub", typeClass, idx);
    }

    @Override
    public Collection unwrap() {
        return shouldIgnoreThisProperty() ? null : Arrays.asList(getValue().getMin(), getValue().getMax());
    }

    @Override
    protected boolean shouldIgnoreThisProperty() {
        Range range = getValue();
        return range == null || (range.getMin() == null && range.getMax() == null);
    }

    private String getTypeOfRange() {
        Object val = getValue().getMin();

        if (val == null) {
            val = getValue().getMax();
        }

        if (val == null) {
            throw new IllegalArgumentException("Empty range provided for query");
        }

        return val.getClass().getName();
    }
}
