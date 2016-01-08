package org.motechproject.mds.query;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.util.LookupName;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The <code>CollectionProperty</code> class represent a property that will be used in JDO query
 * and it has to have the given value(s).
 */
public class CollectionProperty extends AbstractCollectionBasedProperty<Collection> {

    public CollectionProperty(String name, Object value, String type) {
        this(name, value, new ArrayList(), type);
    }

    public CollectionProperty(String name, Object value, Collection collectionType, String type) {
        super(name, collectionType, type);
        addElements(value);
    }

    public CollectionProperty(String jdoVariable, String name, Object value, Collection collectionType, String type) {
        super(jdoVariable, name, collectionType, type);
        addElements(value);
    }

    private void addElements(Object value) {
        if (value instanceof Collection) {
            getValue().addAll((Collection) value);
        } else {
            getValue().add(value);
        }
    }

    @Override
    public CharSequence generateFilter(int idx) {
        Collection<String> strings = new ArrayList<>();
        if (isForRelation()) {
            for (int i = 0; i < getValue().size(); ++i) {
                strings.add(String.format("%s.%s.contains(param%d_%d)", getJdoVariableName(), LookupName.getRelatedFieldName(getName()), idx, i));
            }

            return String.format("(%s.contains(%s) && (%s))", LookupName.getFieldName(getName()), getJdoVariableName(), StringUtils.join(strings, " || "));
        }

        for (int i = 0; i < getValue().size(); ++i) {
            strings.add(String.format("%s.contains(param%d_%d)", getName(), idx, i));
        }

        return String.format("(%s)", StringUtils.join(strings, " || "));
    }
}
