package org.motechproject.mds.util;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.ex.ObjectUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    public static void safeSetCollectionProperty(Object bean, String name, Collection values) {
        try {
            Class collectionType = getPropertyType(bean, name);
            Collection property = instantiateCollection(collectionType);
            for (Object value : values) {
                property.add(value);
            }
            safeSetProperty(bean, name, property);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(
                    "There was a problem with set values {} for property {} in bean: {}",
                    values, name, bean);
            LOGGER.error("Because of: ", e);
        }
    }

    private static Collection instantiateCollection(Class collectionType) {
        if (collectionType.isAssignableFrom(Set.class)) {
            return new HashSet();
        } else if (collectionType.isAssignableFrom(List.class)) {
            return new ArrayList();
        } else if (collectionType.isAssignableFrom(Collection.class)) {
            return new ArrayList();
        } else {
            throw new IllegalArgumentException("Provided class is not a collection");
        }
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

    public static void copyProperties(Object obj, Object target) {
        copyProperties(obj, target, null);
    }

    public static void copyProperties(Object obj, Object target, Set<String> fieldsToCopy) {
        for (PropertyDescriptor descriptor :
                PropertyUtils.getPropertyDescriptors(obj.getClass())) {
            try {
                // skip auto-generated fields and the ones we cannot access
                if (isFieldAutoGenerated(descriptor.getName()) ||
                        !isReadAccessible(descriptor, target) ||
                        !isWriteAccessible(descriptor, obj)) {
                    continue;
                }

                // if we copy only concrete fields, then leave the others alone
                if (fieldsToCopy != null && !fieldsToCopy.contains(descriptor.getName())) {
                    continue;
                }

                Object val = readValue(descriptor, target);
                writeValue(descriptor, obj, val);
            } catch (Exception e) {
                throw new ObjectUpdateException(e);
            }
        }
    }

    public static boolean isReadAccessible(PropertyDescriptor descriptor, Object target) {
        Method readMethod = descriptor.getReadMethod();

        if (readMethod == null) {
            // if no getter we get value through the field
            Field field = ReflectionUtils.findField(target.getClass(), descriptor.getName());
            return fieldAccessible(field);
        } else {
            return true;
        }
    }

    public static boolean isWriteAccessible(PropertyDescriptor descriptor, Object target){
        // setter
        Method writeMethod = descriptor.getWriteMethod();

        if (writeMethod == null) {
            // fallback to the field
            Field field = ReflectionUtils.findField(target.getClass(), descriptor.getName());
            return fieldAccessible(field);
        } else {
            return true;
        }
    }

    public static boolean isFieldAutoGenerated(String fieldName) {
        return ArrayUtils.contains(Constants.Util.GENERATED_FIELD_NAMES, fieldName);
    }

    public static Object readValue(PropertyDescriptor descriptor, Object target)
            throws IllegalAccessException, InvocationTargetException {
        Object val;
        // getter
        Method readMethod = descriptor.getReadMethod();

        if (readMethod == null) {
            // if no getter we get value through the field
            Field field = ReflectionUtils.findField(target.getClass(), descriptor.getName());
            val = field.get(target);
        } else {
            val = readMethod.invoke(target);
        }
        return val;
    }

    public static void writeValue(PropertyDescriptor descriptor, Object target, Object value)
            throws IllegalAccessException, InvocationTargetException {
        // setter
        Method writeMethod = descriptor.getWriteMethod();

        if (writeMethod == null) {
            // fallback to the field
            Field field = ReflectionUtils.findField(target.getClass(), descriptor.getName());
            field.set(target, value);
        } else {
            // call the getter
            writeMethod.invoke(target, value);
        }
    }

    public static boolean fieldAccessible(Field field) {
        return field != null && field.isAccessible();
    }

    public static PropertyDescriptor findDescriptorByName(PropertyDescriptor[] descriptors, String name) {
        for (PropertyDescriptor descriptor : descriptors) {
            if (StringUtils.equals(name, descriptor.getName())) {
                return descriptor;
            }
        }
        return null;
    }
}
