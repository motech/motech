package org.motechproject.mds.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.reflect.MethodUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Utility class handling dynamic setting of field values
 */
public final class FieldHelper {

    public static void setField(Object current, String path, List value) {
        String[] splitPath = path.split("\\.");

        Object target = findTargetForField(current, splitPath);
        setFieldOnTarget(target, splitPath[splitPath.length - 1], value);
    }

    private static void setFieldOnTarget(Object target, String property, List value) {
        try {
            if (property.startsWith("$")) {
                String methodName = property.substring(1);
                Class[] parameterTypes = new Class[null == value ? 0 : value.size()];
                Object[] args = null != value
                        ? value.toArray(new Object[value.size()])
                        : new Object[0];

                for (int i = 0; i < args.length; ++i) {
                    Object item = args[i];
                    parameterTypes[i] = item instanceof List ? List.class : item.getClass();
                }

                MethodUtils.invokeMethod(target, methodName, args, parameterTypes);
            } else {
                PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(target, property);

                if (descriptor == null) {
                    throw new IllegalStateException("Property [" + property + "] not available on class: "
                            + target.getClass().getName());
                } else {
                    PropertyUtils.setProperty(target, property, value.get(0));
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Object findTargetForField(Object start, String[] path) {
        Object current = start;

        for (int i = 0; i < path.length - 1; ++i) {
            String property = path[i];

            if (current == null) {
                throw new IllegalArgumentException("Field on path is null");
            } else if (current instanceof List) {
                int idx = Integer.parseInt(property);
                current = ((List) current).get(idx);
            } else if (current instanceof Map) {
                current = ((Map) current).get(property);
            } else {
                try {
                    current = PropertyUtils.getProperty(current, property);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        return current;
    }

    private FieldHelper() {
    }
}
