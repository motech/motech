package org.motechproject.mds.util;

import org.apache.commons.lang.ArrayUtils;

import java.beans.Introspector;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

public final class MemberUtil {
    public static final String GETTER_PREFIX = "get";
    public static final String SETTER_PREFIX = "set";
    public static final Integer FIELD_NAME_START_IDX = 3;

    private MemberUtil() {
    }

    public static String getFieldName(AnnotatedElement object) {
        String name = null;

        if (object instanceof Method) {
            Method method = (Method) object;

            if (startsWithIgnoreCase(method.getName(), GETTER_PREFIX)
                    || startsWithIgnoreCase(method.getName(), SETTER_PREFIX)) {
                name = method.getName().substring(FIELD_NAME_START_IDX);
                name = Introspector.decapitalize(name);
            }
        } else if (object instanceof java.lang.reflect.Field) {
            java.lang.reflect.Field field = (java.lang.reflect.Field) object;

            name = field.getName();
        }

        return name;
    }

    public static Class<?> getCorrectType(AnnotatedElement object) {
        Class<?> classType = null;

        if (object instanceof Method) {
            Method method = (Method) object;

            if (startsWithIgnoreCase(method.getName(), GETTER_PREFIX)) {
                classType = method.getReturnType();
            } else if (startsWithIgnoreCase(method.getName(), SETTER_PREFIX)) {
                Class<?>[] parameterTypes = method.getParameterTypes();

                if (ArrayUtils.isNotEmpty(parameterTypes)) {
                    classType = parameterTypes[0];
                }
            }
        } else if (object instanceof java.lang.reflect.Field) {
            java.lang.reflect.Field field = (java.lang.reflect.Field) object;

            classType = field.getType();
        }

        return classType;
    }

}
