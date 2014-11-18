package org.motechproject.mds.query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Base for collection based properties
 * @param <T> type of the underlying collection
 */
public abstract class AbstractCollectionBasedProperty<T extends Collection> extends Property<T> {

    protected AbstractCollectionBasedProperty(String name, T value, String type) {
        super(name, value, type);
    }

    @Override
    public CharSequence generateDeclareParameter(int idx) {
        Collection<String> strings = new ArrayList<>();

        for (int i = 0; i < getValue().size(); ++i) {
            strings.add(String.format("%s param%d_%d", getType(), idx, i));
        }

        return StringUtils.join(strings, ", ");
    }

    @Override
    public Collection unwrap() {
        return CollectionUtils.isEmpty(getValue()) ? null : getValue();
    }


    @Override
    protected boolean shouldIgnoreThisProperty() {
        return getValue() == null || getValue().isEmpty();
    }
}
