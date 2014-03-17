package org.motechproject.mds.util;

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
        Object object = InstanceUtil.safeNewInstance(clazz);

        if (null != object) {
            for (PropertyDescriptor descriptor : descriptors) {
                String propertyName = descriptor.getName();

                if (!"class".equalsIgnoreCase(propertyName)) {
                    Object value = PropertyUtil.safeGetProperty(instance, propertyName);
                    PropertyUtil.safeSetProperty(object, propertyName, value);
                }
            }

            for (String exclude : excludes) {
                PropertyUtil.safeSetProperty(object, exclude, null);
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
