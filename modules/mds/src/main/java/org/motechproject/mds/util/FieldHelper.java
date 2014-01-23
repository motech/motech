package org.motechproject.mds.util;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Utility class handling dynamic setting of field values
 */
public class FieldHelper {

    public static void setField(Object current, String property, List value) {
        if (property.startsWith("$")) {
            String methodName = property.substring(1);

            try {
                Class<?> clazz = current.getClass();

                if (value == null) {
                    Method method = clazz.getMethod(methodName);
                    method.invoke(current);
                } else {
                    Class[] classes = new Class[value.size()];
                    for (int i = 0; i < value.size(); ++i) {
                        Object item = value.get(i);
                        classes[i] = item instanceof List ? List.class : item.getClass();
                    }

                    Method method = clazz.getMethod(methodName, classes);
                    method.invoke(current, value.toArray(new Object[value.size()]));
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        } else {
            try {
                Class clazz = PropertyUtils.getProperty(current, property).getClass();
                if (clazz.isEnum()) {
                    PropertyUtils.setProperty(current, property, Enum.valueOf(clazz, (String)value.get(0)));
                } else {
                    PropertyUtils.setProperty(current, property, value.get(0));
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private FieldHelper() {
    }
}
