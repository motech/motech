package org.motechproject.mds.testutil;

import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.FieldMapping;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Utility class for constructing minimalist fields for testing field generation.
 */
public final class FieldTestHelper {

    public static FieldMapping field(String name, Class<?> typeClass) {
        return field(name, typeClass, null);
    }

    public static FieldMapping field(String name, Class<?> typeClass, Object defaultVal) {
        AvailableFieldTypeMapping type = new AvailableFieldTypeMapping();
        // we only need the type
        type.setTypeClass(typeClass.getName());

        FieldMapping field = new FieldMapping();
        // we only need the name, type and default value
        field.setName(name);
        field.setType(type);
        field.setDefaultValue(type.format(defaultVal));

        return field;
    }

    public static Object newVal(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        if (Integer.class.equals(clazz)) {
            return 5;
        } else if (Double.class.equals(clazz)) {
            return 2.1;
        } else if (String.class.equals(clazz)) {
            return "test";
        } else if (List.class.equals(clazz)) {
            return asList("3", "4", "5");
        } else if (Time.class.equals(clazz)) {
            return new Time(10, 54);
        } else if (Boolean.class.equals(clazz)) {
            return true;
        } else {
            return clazz.newInstance();
        }
    }

    private FieldTestHelper() {
    }
}
