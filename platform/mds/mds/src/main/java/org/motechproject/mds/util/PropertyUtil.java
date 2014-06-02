package org.motechproject.mds.util;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The <code>PropertyUtil</code> util class provides the same method like
 * {@link org.apache.commons.beanutils.PropertyUtils} and two additional methods for safe writing
 * and reading property in the given bean.
 */
public final class PropertyUtil extends PropertyUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

    private PropertyUtil() {
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Object bean) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean);

        for (PropertyDescriptor descriptor : descriptors) {
            boolean isBoolean = Boolean.class.isAssignableFrom(descriptor.getPropertyType());
            boolean isPrimitiveBoolean = boolean.class.isAssignableFrom(descriptor.getPropertyType());
            boolean hasReadMethod = null != descriptor.getReadMethod();

            if ((isBoolean || isPrimitiveBoolean) && !hasReadMethod) {
                String propName = StringUtils.capitalize(descriptor.getName());
                Class<?> beanClass = bean.getClass();

                Method get = MethodUtils.getMatchingAccessibleMethod(beanClass, "get" + propName, new Class[0]);
                Method is = MethodUtils.getMatchingAccessibleMethod(beanClass, "is" + propName, new Class[0]);

                try {
                    if (null != get) {
                        descriptor.setReadMethod(get);
                    } else if (null != is) {
                        descriptor.setReadMethod(is);
                    }
                } catch (IntrospectionException e) {
                    LOGGER.error("Can't set read method for property: {}", propName);
                    LOGGER.error("because of ", e);
                }
            }
        }

        return descriptors;

    }

    public static void safeSetProperty(Object bean, String name, Object value) {
        try {
            if (null != bean && isWriteable(bean, name)) {
                setProperty(bean, name, value);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(
                    "There was a problem with set value {} for property {} in bean: {}",
                    new Object[]{value, name, bean}
            );
            LOGGER.error("Because of: ", e);
        }
    }

    public static Object safeGetProperty(Object bean, String name) {
        Object value = null;

        try {
            if (null != bean && isReadable(bean, name)) {
                value = getProperty(bean, name);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(
                    "There was a problem with get value of property {} in bean: {}", name, bean
            );
            LOGGER.error("Because of: ", e);
        }

        return value;
    }

}
