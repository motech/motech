package org.motechproject.mds.testutil;

import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.util.TypeHelper;
import org.motechproject.mds.web.domain.FieldRecord;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Utility class for constructing minimalist fields for testing field generation.
 */
public final class FieldTestHelper {

    public static Field field(Long id, String name, Class<?> typeClass) {
        return field(name, typeClass, null, id);
    }

    public static Field field(String name, Class<?> typeClass) {
        return field(name, typeClass, null);
    }

    public static Field field(String name, Class<?> typeClass, Object defaultVal) {
        return field(name, typeClass, defaultVal, null);
    }

    public static Field field(String name, Class<?> typeClass, Object defaultVal, Long id) {
        Type type = new Type();
        // we only need the type
        type.setTypeClass(typeClass);

        Field field = new Field();
        // we only need the name, type and default value
        field.setName(name);
        field.setType(type);
        field.setDefaultValue(TypeHelper.format(defaultVal));
        field.setId(id);

        return field;
    }

    public static Object newVal(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        if (Integer.class.equals(clazz)) {
            return 5;
        } else if (Long.class.equals(clazz)) {
            return 5L;
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

    public static FieldDto fieldDto(String name, Class<?> clazz) {
        return fieldDto(name, clazz.getName());
    }

    public static FieldDto fieldDto(String name, String className) {
        return fieldDto(null, name, className, null, null);
    }

    public static FieldDto fieldDto(Long id, String name, String className,
                                    String displayName, Object defValue) {
        FieldDto fieldDto = new FieldDto();
        fieldDto.setType(new TypeDto(className, "", "", className));
        fieldDto.setBasic(new FieldBasicDto(displayName, name));
        fieldDto.getBasic().setDefaultValue(defValue);
        fieldDto.setId(id);
        return fieldDto;
    }

    public static FieldRecord fieldRecord(String name, String className, String displayName,
                                          Object value) {
        TypeDto type = new TypeDto(className, "", "", className);
        return new FieldRecord(name, displayName, value, type);
    }

    public static LookupFieldDto lookupFieldDto(String name) {
        return lookupFieldDto(null, name);
    }

    public static LookupFieldDto lookupFieldDto(Long id, String name) {
        return new LookupFieldDto(id, name, LookupFieldDto.Type.VALUE);
    }

    public static List<LookupFieldDto> lookupFieldDtos(String... names) {
        List<LookupFieldDto> lookupFields = new ArrayList<>();
        for (String name : names) {
            lookupFields.add(new LookupFieldDto(null, name, LookupFieldDto.Type.VALUE));
        }
        return lookupFields;
    }

    private FieldTestHelper() {
    }
}
