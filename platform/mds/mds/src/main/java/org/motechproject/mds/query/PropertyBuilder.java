package org.motechproject.mds.query;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

            if (holder.isCollection()) {
                if (holder.getTypeClassName().equals(List.class.getName())) {
                    return new CollectionProperty(name, value, new ArrayList(), holder.getUnderlyingType());
                } else if (holder.getTypeClassName().equals(Set.class.getName())) {
                    return new CollectionProperty(name, value, new HashSet(), holder.getUnderlyingType());
                }
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
            switch (operator) {
                case Constants.Operators.MATCHES:
                    return new MatchesProperty(name, (String) value);
                case Constants.Operators.MATCHES_CASE_INSENSITIVE:
                    return new MatchesCaseInsensitiveProperty(name, (String) value);
                case Constants.Operators.EQ_IGNORE_CASE:
                    if (String.class.getName().equals(type)) {
                        return new EqualsCaseInsensitiveProperty(name, (String) value, type, operator);
                    }
                default:
                    return new CustomOperatorProperty<>(name, value, type, operator);
            }
        } else {
            return new EqualProperty<>(name, value, type);
        }
    }

    public static Property createRelationProperty(String jdoVariableName, String name, Object value, String type) {
        return createRelationProperty(jdoVariableName, name, value, type, null);
    }

    public static Property createRelationProperty(String jdoVariableName, String name, Object value, String type, String operator) {
        if (value instanceof Set) {
            Set set = (Set) value;
            return new SetProperty<>(jdoVariableName, name, set, type);
        } else if (value instanceof Range) {
            Range range = (Range) value;
            return new RangeProperty<>(jdoVariableName, name, range, type);
        } else if (StringUtils.isNotBlank(operator)) {
            if (Constants.Operators.MATCHES.equals(operator)) {
                return new MatchesProperty(jdoVariableName, name, (String) value);
            } else {
                return new CustomOperatorProperty<>(jdoVariableName, name, value, type, operator);
            }
        } else {
            return new EqualProperty<>(jdoVariableName, name, value, type);
        }
    }

    public static CollectionProperty createRelationPropertyForComboboxCollection(String jdoVariable, String name, Object value, String type) {
        return new CollectionProperty(jdoVariable, name, value, new ArrayList(), type);
    }

}
