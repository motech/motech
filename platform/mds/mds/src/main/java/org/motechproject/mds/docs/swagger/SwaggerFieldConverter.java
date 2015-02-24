package org.motechproject.mds.docs.swagger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.mds.docs.swagger.model.Property;
import org.motechproject.mds.domain.FieldInfo;

import java.util.Date;

import static org.motechproject.mds.docs.swagger.SwaggerConstants.ARRAY_TYPE;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.BOOLEAN_TYPE;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.BYTE_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DATETIME_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DATE_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DOUBLE_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INT32_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INT64_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INTEGER_TYPE;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.NUMBER_TYPE;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.STRING_TYPE;

/**
 * Utility for converting MDS field types to swagger types.
 */
public final class SwaggerFieldConverter {

    /**
     * Converts the given field based on its type to a Swagger property.
     * @param field the field to be converted
     * @return the Swagger property version of the given fields type
     */
    public static Property fieldToProperty(FieldInfo field) {
        final String typeClass = field.getType();

        Property property = toNumberProperty(typeClass);
        if (property != null) {
            return property;
        }
        property = toDateProperty(typeClass);
        if (property != null) {
            return property;
        }
        property = toComboboxProperty(field.getTypeInfo());
        if (property != null) {
            return property;
        }

        return toMiscProperty(field.getTypeInfo());
    }

    private static Property toComboboxProperty(FieldInfo.TypeInfo typeInfo) {
        if (typeInfo.isCombobox()) {
            Property itemProperty = new Property(STRING_TYPE);

            // user-supplied comoboxes are actually strings or list of strings
            if (!typeInfo.isAllowUserSupplied()) {
                itemProperty.setEnumValues(typeInfo.getItems());
            }

            if (typeInfo.isAllowsMultipleSelection()) {
                return new Property(ARRAY_TYPE, itemProperty);
            } else {
                return itemProperty;
            }
        } else {
            return null;
        }
    }

    private static Property toDateProperty(String typeClass) {
        if (eq(Date.class, typeClass)) {
            return new Property(STRING_TYPE, DATETIME_FORMAT);
        } else if (eq(LocalDate.class, typeClass)) {
            return new Property(STRING_TYPE, DATE_FORMAT);
        } else if (eq(DateTime.class, typeClass) || eq(Date.class, typeClass)) {
            return new Property(STRING_TYPE, DATETIME_FORMAT);
        } else {
            return null;
        }
    }

    private static Property toNumberProperty(String typeClass) {
        if (eq(Integer.class, typeClass)) {
            return new Property(INTEGER_TYPE, INT32_FORMAT);
        } else if (eq(Long.class, typeClass)) {
            return new Property(INTEGER_TYPE, INT64_FORMAT);
        } else if (eq(Double.class, typeClass)) {
            return new Property(NUMBER_TYPE, DOUBLE_FORMAT);
        } else {
            return null;
        }
    }

    private static Property toMiscProperty(FieldInfo.TypeInfo typeInfo) {
        String typeClass = typeInfo.getType();
        if (eq(String.class, typeClass)) {
            return new Property(STRING_TYPE);
        } else if (eq(Byte[].class, typeClass)) {
            return new Property(STRING_TYPE, BYTE_FORMAT);
        } else if (eq(Boolean.class, typeClass)) {
            return new Property(BOOLEAN_TYPE);
        } else {
            // String for other types
            return new Property(STRING_TYPE);
        }
    }

    private static boolean eq(Class clazz, String typeClass) {
        return clazz.getName().equals(typeClass);
    }

    private SwaggerFieldConverter() {
    }
}
