package org.motechproject.mds.query;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.util.Constants;

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
                return new CollectionProperty(name, value, holder.getUnderlyingType());
            }
        }

        return create(name, value, field.getType().getTypeClassName());
    }

    public static Property create(String name, Object value, Class type) {
        return create(name, value, type.getName(), null);
    }

    public static Property create(String name, Object value, String type) {
        return create(name, value, type, null);
    }

    public static Property create(String name, Object value, String type, String operator) {
        if (value instanceof Set) {
            Set set = (Set) value;
            return new SetProperty<>(name, set, type);
        } else if (value instanceof Range) {
            Range range = (Range) value;
            return new RangeProperty<>(name, range, type);
        } else if (StringUtils.isNotBlank(operator)) {
            if (Constants.Operators.MATCHES.equals(operator)) {
                return new MatchesProperty(name, (String) value);
            } else {
                return new CustomOperatorProperty<>(name, value, type, operator);
            }
        } else {
            return new EqualProperty<>(name, value, type);
        }
    }

}
