package org.motechproject.mds.query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The <code>CollectionProperty</code> class represent a property that will be used in JDO query
 * and it has to have the given value(s).
 */
public class CollectionProperty extends Property<Collection> {

    public CollectionProperty(String name, Object value) {
        super(name, new ArrayList());

        if (value instanceof Collection) {
            getValue().addAll((Collection) value);
        } else {
            getValue().add(value);
        }
    }

    @Override
    public CharSequence generateFilter(int idx) {
        Collection<String> strings = new ArrayList<>();

        for (int i = 0; i < getValue().size(); ++i) {
            strings.add(String.format("%s.contains(param%d_%d)", getName(), idx, i));
        }

        return String.format("(%s)", StringUtils.join(strings, " || "));
    }

    @Override
    public CharSequence generateDeclareParameter(int idx) {
        String typeClass = getTypeOfCollection();
        Collection<String> strings = new ArrayList<>();

        for (int i = 0; i < getValue().size(); ++i) {
            strings.add(String.format("%s param%d_%d", typeClass, idx, i));
        }

        return StringUtils.join(strings, ", ");
    }

    @Override
    public Collection unwrap() {
        return getValue();
    }

    @Override
    protected boolean shouldIgnoreThisProperty() {
        return CollectionUtils.isEmpty(getValue()) || containsOnlyNullValues(getValue());
    }

    private String getTypeOfCollection() {
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
