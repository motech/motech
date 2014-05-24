package org.motechproject.mds.query;

import org.motechproject.commons.api.Range;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;

import java.util.Set;

/**
 * The <code>PropertyBuilder</code> class is a util class that helps create appropriate property
 * class based on passed name and value.
 */
public final class PropertyBuilder {

    private PropertyBuilder() {
    }

    public static Property create(Field field, Object value) {
        String name = field.getName();
        Type type = field.getType();

        if (type.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(field);

            if (holder.isStringList() || holder.isEnumList()) {
                return new CollectionProperty(name, value);
            }
        }

        return create(name, value);
    }

    public static Property create(String name, Object value) {
        if (value instanceof Set) {
            Set set = (Set) value;
            return new SetProperty<>(name, set);
        } else if (value instanceof Range) {
            Range range = (Range) value;
            return new RangeProperty<>(name, range);
        } else {
            return new EqualProperty<>(name, value);
        }
    }

}
