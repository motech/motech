package org.motechproject.mds.web.rest.docs.swagger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.web.rest.docs.swagger.model.Property;
import org.motechproject.mds.util.Constants;

import java.util.Date;
import java.util.List;

import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.ARRAY_TYPE;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.BOOLEAN_TYPE;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.BYTE_FORMAT;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.DATETIME_FORMAT;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.DATE_FORMAT;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.DOUBLE_FORMAT;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.INT32_FORMAT;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.INT64_FORMAT;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.INTEGER_TYPE;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.NUMBER_TYPE;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.STRING_TYPE;

/**
 * Utility for converting MDS field types to swagger types.
 */
public final class SwaggerFieldConverter {

    public static Property fieldToProperty(FieldDto field) {
        final String typeClass = field.getType().getTypeClass();

        if (eq(String.class, typeClass)) {
            return new Property(STRING_TYPE);
        } else if (eq(Integer.class, typeClass)) {
            return new Property(INTEGER_TYPE, INT32_FORMAT);
        } else if (eq(Long.class, typeClass)) {
            return new Property(INTEGER_TYPE, INT64_FORMAT);
        } else if (eq(Double.class, typeClass)) {
            return new Property(NUMBER_TYPE, DOUBLE_FORMAT);
        } else if (eq(Byte[].class, typeClass)) {
            return new Property(ARRAY_TYPE, new Property(STRING_TYPE, BYTE_FORMAT));
        } else if (eq(Boolean.class, typeClass)) {
            return new Property(BOOLEAN_TYPE);
        } else if (eq(LocalDate.class, typeClass)) {
            return new Property(STRING_TYPE, DATE_FORMAT);
        } else if (eq(DateTime.class, typeClass) || eq(Date.class, typeClass)) {
            return new Property(STRING_TYPE, DATETIME_FORMAT);
        } else if (eq(List.class, typeClass) &&
                Constants.Util.TRUE.equals(field.getSettingsValueAsString(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS))) {
            return new Property(ARRAY_TYPE, new Property(STRING_TYPE));
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
