package org.motechproject.mds.docs.swagger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.mds.docs.swagger.model.Parameter;
import org.motechproject.mds.docs.swagger.model.Property;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Field;

import java.util.Arrays;
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
    public static Property fieldToProperty(Field field) {
        final String typeClass = field.getType().getTypeClassName();

        Property property = toNumberProperty(typeClass);
        if (property != null) {
            return property;
        }
        property = toDateProperty(typeClass);
        if (property != null) {
            return property;
        }
        property = toComboboxProperty(field);
        if (property != null) {
            return property;
        }

        return toMiscProperty(typeClass);
    }

    public static Parameter fieldToParameter(Field field) {
        Property property = fieldToProperty(field);

        Parameter parameter = new Parameter(property);

        parameter.setName(field.getName());
        parameter.setDescription();

        return parameter;
    }

    private static Property toComboboxProperty(Field field) {
        if (field.getType().isCombobox()) {
            ComboboxHolder cbHolder = new ComboboxHolder(field);

            Property itemProperty = new Property(STRING_TYPE);

            // user-supplied comoboxes are actually strings or list of strings
            if (!cbHolder.isAllowUserSupplied()) {
                itemProperty.setEnumValues(Arrays.asList(cbHolder.getValues()));
            }

            if (cbHolder.isAllowMultipleSelections()) {
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

    private static Property toMiscProperty(String typeClass) {
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
