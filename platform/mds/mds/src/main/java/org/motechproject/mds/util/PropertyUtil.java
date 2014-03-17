package org.motechproject.mds.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * The <code>PropertyUtil</code> util class provides the same method like
 * {@link org.apache.commons.beanutils.PropertyUtils} and two additional methods for safe writing
 * and reading property in the given bean.
 */
public final class PropertyUtil extends PropertyUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

    private PropertyUtil() {
    }

    public static void safeSetProperty(Object bean, String name, Object value) {
        try {
            if (isWriteable(bean, name)) {
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
            if (isReadable(bean, name)) {
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
