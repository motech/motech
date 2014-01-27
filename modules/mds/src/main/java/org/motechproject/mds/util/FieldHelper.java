package org.motechproject.mds.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Utility class handling dynamic setting of field values
 */
public final class FieldHelper {

    public static void setField(Object current, String property, List value) {
        Class clazz = current.getClass();

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

                MethodUtils.invokeMethod(current, methodName, args, parameterTypes);
            } else {
                Field field = FieldUtils.getDeclaredField(clazz, property, true);

                if (field.isEnumConstant()) {
                    Enum enumValue = Enum.valueOf(clazz, (String) value.get(0));
                    PropertyUtils.setProperty(current, property, enumValue);
                } else {
                    PropertyUtils.setProperty(current, property, value.get(0));
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private FieldHelper() {
    }
}
