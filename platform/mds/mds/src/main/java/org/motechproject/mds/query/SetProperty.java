package org.motechproject.mds.query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * The <code>SetProperty</code> class represents a property that will be used in JDO query
 * and it has to have one of the value from the given set.
 *
 * @param <T> type used in set.
 */
public class SetProperty<T> extends Property<Set<T>> {

    public SetProperty(String name, Set<T> value) {
        super(name, value);
    }

    @Override
    public CharSequence generateFilter(int idx) {
        Collection<String> strings = new ArrayList<>();

        for (int i = 0; i < getValue().size(); ++i) {
            strings.add(String.format("%s == param%d_%d", getName(), idx, i));
        }

        return String.format("(%s)", StringUtils.join(strings, " || "));
    }

    @Override
    public CharSequence generateDeclareParameter(int idx) {
        String typeClass = getTypeOfSet();
        Collection<String> strings = new ArrayList<>();

        for (int i = 0; i < getValue().size(); ++i) {
            strings.add(String.format("%s param%d_%d", typeClass, idx, i));
        }

        return StringUtils.join(strings, ", ");
    }

    @Override
    public Collection unwrap() {
        return (shouldIgnoreThisProperty()) ? null : getValue();
    }

    @Override
    protected boolean shouldIgnoreThisProperty() {
        return CollectionUtils.isEmpty(getValue()) || containsOnlyNullValues(getValue());
    }

    private String getTypeOfSet() {
        Object val = null;

        if (CollectionUtils.isNotEmpty(getValue())) {
            val = getValue().iterator().next();
        }

        if (val == null) {
            throw new IllegalArgumentException("Empty set provided for query");
        }

        return val.getClass().getName();
    }
}
