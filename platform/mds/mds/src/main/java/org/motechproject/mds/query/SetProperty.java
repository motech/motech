package org.motechproject.mds.query;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.util.LookupName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * The <code>SetProperty</code> class represents a property that will be used in JDO query
 * and it has to have one of the value from the given set.
 *
 * @param <T> type used in set.
 */
public class SetProperty<T> extends AbstractCollectionBasedProperty<Set<T>> {

    public SetProperty(String name, Set<T> value, String type) {
        super(name, value, type);
    }

    public SetProperty(String jdoVariableName, String name, Set<T> value, String type) {
        super(jdoVariableName, name, value, type);
    }

    @Override
    public CharSequence generateFilter(int idx) {
        Collection<String> strings = new ArrayList<>();
        if (isForRelation()) {
            for (int i = 0; i < getValue().size(); ++i) {
                strings.add(String.format("%s.%s == param%d_%d", getJdoVariableName(), LookupName.getRelatedFieldName(getName()), idx, i));
            }

            String params = StringUtils.join(strings, " || ");
            if (StringUtils.isNotBlank(params)) {
                return String.format("(%s.contains(%s) && (%s))", LookupName.getFieldName(getName()), getJdoVariableName(), params);
            }
            return params;
        } else {
            for (int i = 0; i < getValue().size(); ++i) {
                strings.add(String.format("%s == param%d_%d", getName(), idx, i));
            }

            return String.format("(%s)", StringUtils.join(strings, " || "));
        }
    }

    @Override
    public Collection unwrap() {
        return getValue();
    }
}
