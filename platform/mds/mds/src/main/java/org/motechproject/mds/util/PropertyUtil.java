package org.motechproject.mds.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.exception.object.PropertyCopyException;
import org.motechproject.mds.exception.object.PropertyReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The <code>PropertyUtil</code> util class provides the same method like
 * {@link org.apache.commons.beanutils.PropertyUtils} and two additional methods for safe writing
 * and reading property in the given bean.
 */
public final class PropertyUtil extends PropertyUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

    private static final NoOpConverter NO_OP_CONVERTER = new NoOpConverter();

    private PropertyUtil() {
    }

    public static void safeSetCollectionProperty(Object bean, String name, Collection values) {
        try {
            Class collectionType = getPropertyType(bean, name);
            Collection property = instantiateCollection(collectionType);
            for (Object value : values) {
                property.add(value);
            }
            safeSetProperty(bean, name, property);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(
                    "There was a problem with set values {} for property {} in bean: {}",
                    values, name, bean, e);
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
            if (null != bean) {
                if (isWriteable(bean, name)) {
                    setProperty(bean, name, value);
                } else if (Character.isUpperCase(name.charAt(0))) {
                    safeSetProperty(bean, StringUtils.uncapitalize(name), value);
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(
                    "There was a problem with set value {} for property {} in bean: {}",
                    value, name, bean, e);
        }
    }

    public static Object safeGetProperty(Object bean, String name) {
        Object value = null;

        try {
            if (null != bean) {
                if (isReadable(bean, name)) {
                    value = getProperty(bean, name);
                } else if (Character.isUpperCase(name.charAt(0))) {
                    return safeGetProperty(bean, StringUtils.uncapitalize(name));
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(
                    "There was a problem with get value of property {} in bean: {}", name, bean, e
            );
        }

        return value;
    }

    public static Class<?> safeGetPropertyType(Object bean, String name) {
        Class<?> type = null;
        try {
            if (null != bean && isReadable(bean, name)) {
                type = getPropertyType(bean, name);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("Cannot get property type of {} in {}: ", name, bean, e);
        }
        return type;
    }

    public static void copyProperties(Object target, Object object) {
        copyProperties(target, object, null, null);
    }

    public static void copyProperties(Object target, Object object, ValueConverter converter) {
        copyProperties(target, object, converter, null);
    }

    public static void copyProperties(Object target, Object object, ValueConverter converter,
                                      Set<String> fieldsToUpdate) {
        ValueConverter converterToUse = converter == null ? NO_OP_CONVERTER : converter;

        Class objectClass = object.getClass();

        for (PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(objectClass)) {

            if (shouldSkipField(descriptor, objectClass, fieldsToUpdate)) {
                continue;
            }

            try {
                // target and value can have different classes - for example when copying to history
                PropertyDescriptor targetDescriptor = PropertyUtils.getPropertyDescriptor(target, descriptor.getName());
                if (targetDescriptor == null) {
                    // skip if this field is not present in the target
                    continue;
                }

                Object val = readValue(object, descriptor, converterToUse);
                writeValue(target, val, targetDescriptor);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | RuntimeException e) {
                throw new PropertyCopyException("Unable to copy properties for " + objectClass.getName(), e);
            }
        }
    }

    public static List<String> findChangedFields(Object newInstance, Object oldInstance) {
        return findChangedFields(newInstance, oldInstance, null);
    }

    public static List<String> findChangedFields(Object newInstance, Object oldInstance, ValueConverter valueConverter) {
        ValueConverter converterToUse = valueConverter == null ? NO_OP_CONVERTER : valueConverter;

        Class objectClass = newInstance.getClass();

        List<String> changedProperties = new ArrayList<>();

        for (PropertyDescriptor newValueDescriptor : PropertyUtils.getPropertyDescriptors(objectClass)) {
            String fieldName = newValueDescriptor.getName();

            // skip the id field
            if (Constants.Util.ID_FIELD_NAME.equals(fieldName)) {
                continue;
            }

            if (readWriteAccessible(objectClass, newValueDescriptor)) {
                try {
                    // target and value can have different classes - for example when copying to history
                    PropertyDescriptor oldValueDescription = PropertyUtils.getPropertyDescriptor(oldInstance, fieldName);
                    // check only if two descriptors are available
                    if (oldValueDescription != null) {
                        Object newValue = readValue(newInstance, newValueDescriptor, converterToUse);
                        Object oldValue = readValue(oldInstance, oldValueDescription, converterToUse);

                        if (!Objects.equals(newValue, oldValue)) {
                            changedProperties.add(fieldName);
                        }
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | RuntimeException e) {
                    throw new PropertyReadException("Unable to compare properties for " + objectClass.getName(), e);
                }
            }
        }

        return changedProperties;
    }

    private static boolean shouldSkipField(PropertyDescriptor descriptor, Class objectClass,
                                           Set<String> fieldsToUpdate) {
        if (fieldsToUpdate != null && !fieldsToUpdate.contains(descriptor.getName())) {
            // if we have a list of fields to update, then skip if this field is not on it
            return true;
        }

        if (fieldsToUpdate == null && ArrayUtils.contains(Constants.Util.GENERATED_FIELD_NAMES,
                descriptor.getName())) {
            // we skip generated fields unless we have fields explicitly provided
            return true;
        }

        if (fieldsToUpdate == null && Constants.Util.INSTANCE_VERSION_FIELD_NAME.equals(descriptor.getName())) {
            // skip the version field
            return true;
        }

        if (!readWriteAccessible(objectClass, descriptor)) {
            return true;
        }

        return false;
    }

    private static boolean readWriteAccessible(Class objectClass, PropertyDescriptor descriptor) {
        Method readMethod = descriptor.getReadMethod();
        if (readMethod == null) {
            Field field = ReflectionUtils.findField(objectClass, descriptor.getName());
            if (fieldNotAccessible(field)) {
                return false;
            }
        }

        Method writeMethod = descriptor.getWriteMethod();
        if (writeMethod == null) {
            Field field = ReflectionUtils.findField(objectClass, descriptor.getName());
            if (fieldNotAccessible(field)) {
                return false;
            }
        }

        return true;
    }

    private static Object readValue(Object obj, PropertyDescriptor descriptor, ValueConverter converter)
            throws InvocationTargetException, IllegalAccessException {
        Method readMethod = descriptor.getReadMethod();

        Object val;

        if (readMethod == null) {
            // if no getter we get value through the field
            Field field = ReflectionUtils.findField(obj.getClass(), descriptor.getName());
            val = field.get(obj);
        } else {
            val = readMethod.invoke(obj);
        }

        return converter.convert(val, descriptor);
    }

    private static void writeValue(Object target, Object val, PropertyDescriptor descriptor)
            throws InvocationTargetException, IllegalAccessException {
        Method writeMethod = descriptor.getWriteMethod();

        if (writeMethod == null) {
            // fallback to the field
            Field field = ReflectionUtils.findField(target.getClass(), descriptor.getName());
            // set the field value
            field.set(target, val);
        } else {
            // call the getter
            writeMethod.invoke(target, val);
        }
    }

    private static boolean fieldNotAccessible(Field field) {
        return field == null || !field.isAccessible();
    }

    public interface ValueConverter {
        Object convert(Object value, PropertyDescriptor descriptor);
    }

    public static class NoOpConverter implements ValueConverter {
        @Override
        public Object convert(Object value, PropertyDescriptor descriptor) {
            return value;
        }
    }
}
