package org.motechproject.mds.util;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;

/**
 * The <code>InstanceUtil</code> util class contains methods to get some information from the
 * given instance of entity or create a new copy of instance and cast it to the given class.
 */
public final class InstanceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceUtil.class);

    private InstanceUtil() {
    }

    public static Object safeNewInstance(Class<?> definition) {
        Object record = null;

        try {
            record = definition.newInstance();
        } catch (Exception e) {
            LOGGER.error("There was a problem with creating new instance of {}", definition);
            LOGGER.error("Because of: ", e);
        }

        return record;
    }

    public static <T> Object copy(Class<T> clazz, Object instance, String exclude) {
        return copy(clazz, instance, new String[]{exclude});
    }

    public static <T> Object copy(Class<T> clazz, Object instance, String[] excludes) {
        PropertyDescriptor[] descriptors = PropertyUtil.getPropertyDescriptors(instance);
        Object object = safeNewInstance(clazz);

        if (null != object) {
            try {
                for (PropertyDescriptor descriptor : descriptors) {
                    String propertyName = descriptor.getName();

                    if (!"class".equalsIgnoreCase(propertyName)) {
                        Object value = descriptor.getReadMethod().invoke(instance);
                        Class<?> parameterClass = descriptor.getPropertyType();

                        if (!TypeHelper.isPrimitive(parameterClass)) {
                            // the value should be from the same class loader as history object
                            ClassLoader classLoader = object.getClass().getClassLoader();
                            String valueAsString = null == value ? null : value.toString();

                            value = TypeHelper.parse(valueAsString, parameterClass.getName(), classLoader);
                        }

                        if (null != value) {
                            PropertyUtil.safeSetProperty(object, propertyName, value);
                        }
                    }
                }

                for (PropertyDescriptor descriptor : descriptors) {
                    String propertyName = descriptor.getName();

                    if (ArrayUtils.contains(excludes, propertyName)) {
                        PropertyUtil.safeSetProperty(object, propertyName, null);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("There was a problem with setting properties in ", object);
                LOGGER.error("because of: ", e);
            }
        }

        return object;
    }

    public static Long getInstanceId(Object instance) {
        Object value = PropertyUtil.safeGetProperty(instance, "id");
        Number id = null;

        if (value instanceof Number) {
            id = (Number) value;
        }

        return null == id ? null : id.longValue();
    }

    public static String getInstanceClassName(Object instance) {
        return null == instance ? "" : instance.getClass().getName();
    }

}
