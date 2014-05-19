package org.motechproject.mds.util.instance;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.ServiceNotFoundException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    public static <T> Object copy(EntityDto entity, Class<T> clazz, Object instance, String exclude) {
        return copy(entity, clazz, instance, new String[]{exclude});
    }

    public static <T> Object copy(EntityDto entity, Class<T> clazz, Object instance, String[] excludes) {
        PropertyDescriptor[] descriptors = PropertyUtil.getPropertyDescriptors(instance);
        Object object = safeNewInstance(clazz);
        BundleContext bundleContext = FrameworkUtil.getBundle(instance.getClass()) != null ?
                FrameworkUtil.getBundle(instance.getClass()).getBundleContext() : null;

        if (null != object) {
            try {
                for (PropertyDescriptor descriptor : descriptors) {
                    String propertyName = descriptor.getName();

                    if (shouldSetProperty(instance, propertyName)) {
                        Method method = descriptor.getReadMethod();
                        Object value = getValue(object, descriptor, method, entity, instance, propertyName, bundleContext);

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

    private static Object getValue(Object object, PropertyDescriptor descriptor, Method method, EntityDto entity, Object instance, String propertyName, BundleContext bundleContext) throws InvocationTargetException, IllegalAccessException {
        Object value;
        if (Byte[].class.equals(method.getReturnType())) {
            value = getServiceForEntity(entity, bundleContext).getDetachedField(instance, propertyName);
        } else {
            value = method.invoke(instance);
        }

        Class<?> parameterClass = descriptor.getPropertyType();

        if (!TypeHelper.isPrimitive(parameterClass) && !Byte[].class.equals(parameterClass)) {
            // the value should be from the same class loader as history object
            ClassLoader classLoader = object.getClass().getClassLoader();
            String valueAsString = null == value ? null : value.toString();

            value = TypeHelper.parse(valueAsString, parameterClass.getName(), classLoader);
        }
        return value;
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

    private static boolean shouldSetProperty(Object instance, String propertyName) {
        return !"class".equalsIgnoreCase(propertyName) &&
                PropertyUtil.safeGetProperty(instance, propertyName) != null;
    }

    private static MotechDataService getServiceForEntity(EntityDto entity, BundleContext bundleContext) {
        String className = entity.getClassName();
        String interfaceName = MotechClassPool.getInterfaceName(className);
        ServiceReference ref = bundleContext.getServiceReference(interfaceName);

        if (ref == null) {
            throw new ServiceNotFoundException();
        }

        return (MotechDataService) bundleContext.getService(ref);
    }
}
