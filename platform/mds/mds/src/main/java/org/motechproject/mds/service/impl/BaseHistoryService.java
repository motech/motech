package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.ex.ServiceNotFoundException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManagerFactory;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;

public abstract class BaseHistoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseHistoryService.class);

    private PersistenceManagerFactory persistenceManagerFactory;
    private BundleContext bundleContext;
    private AllEntities allEntities;

    protected Long getEntitySchemaVersion(Object src) {
        String instanceClassName = getInstanceClassName(src);
        return allEntities.retrieveByClassName(instanceClassName).getEntityVersion();
    }

    protected Long getCurrentSchemaVersion(String className) {
        return allEntities.retrieveByClassName(className).getEntityVersion();
    }

    protected Entity getEntity(Long id) {
        return allEntities.retrieveById(id);
    }

    protected List<Entity> getEntities() {
        List<Entity> list = new ArrayList<>();

        for (Entity entity : allEntities.retrieveAll()) {
            if (entity.isActualEntity()) {
                list.add(entity);
            }
        }

        return list;
    }

    protected Long getInstanceId(Object instance) {
        Object value = PropertyUtil.safeGetProperty(instance, "id");
        Number id = null;

        if (value instanceof Number) {
            id = (Number) value;
        }

        return null == id ? null : id.longValue();
    }

    protected String getInstanceClassName(Object instance) {
        return null == instance ? "" : instance.getClass().getName();
    }

    @Transactional
    protected <T> Object create(Class<T> clazz, Object src, EntityType type) {
        Entity entity = allEntities.retrieveByClassName(src.getClass().getName());
        PropertyDescriptor[] descriptors = PropertyUtil.getPropertyDescriptors(src);
        Object target;

        try {
            target = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("There was a problem with creating new instance of {}", clazz);
            throw new IllegalStateException(e);
        }

        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            Object value = PropertyUtil.safeGetProperty(src, name);

            if (!"class".equalsIgnoreCase(name) && value != null) {
                value = getValue(descriptor, src, target, name, entity, type);

                if (null != value) {
                    PropertyUtil.safeSetProperty(target, name, value);
                }
            }
        }

        PropertyUtil.safeSetProperty(target, "id", null);

        return target;
    }

    protected Object getValue(PropertyDescriptor descriptor, Object src, Object target,
                              String property, Entity entity, EntityType type) {
        Field field = entity.getField(property);
        Type fieldType = field.getType();

        Method read = descriptor.getReadMethod();

        Object value = null;

        try {
            value = isByteArray(read.getReturnType())
                    ? findService(src.getClass()).getDetachedField(src, property)
                    : read.invoke(src);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("There was a problem with getting property [{}] value from {}", property, src);
            LOGGER.error("because of: ", e);
        }

        Class<?> parameter = descriptor.getPropertyType();

        if (fieldType.isRelationship()) {
            FieldMetadata metadata = field.getMetadata(RELATED_CLASS);
            String className = metadata.getValue();

            Class<?> clazz = getClass(className, type);

            if (value instanceof Collection) {
                Collection collection = (Collection) value;
                List tmp = new ArrayList();

                for (Object element : collection) {
                    Object item = create(clazz, element, type);
                    tmp.add(item);
                }

                value = tmp;
            } else {
                value = create(clazz, value, type);
            }
        } else if (!TypeHelper.isPrimitive(parameter) && !isByteArray(parameter)) {
            // the value should be from the same class loader as history object
            ClassLoader classLoader = target.getClass().getClassLoader();
            String valueAsString = null == value ? null : value.toString();

            value = TypeHelper.parse(valueAsString, parameter.getName(), classLoader);
        }

        return value;
    }

    protected MotechDataService findService(Class<?> clazz) {
        String interfaceName = MotechClassPool.getInterfaceName(clazz.getName());
        ServiceReference ref = bundleContext.getServiceReference(interfaceName);

        if (ref == null) {
            throw new ServiceNotFoundException();
        }

        return (MotechDataService) bundleContext.getService(ref);
    }

    protected Class<?> getClass(Object src, EntityType type) {
        return getClass(getInstanceClassName(src), type);
    }

    protected Class<?> getClass(String srcClassName, EntityType type) {
        String className;

        switch (type) {
            case HISTORY:
                className = ClassName.getHistoryClassName(srcClassName);
                break;
            case TRASH:
                className = ClassName.getTrashClassName(srcClassName);
                break;
            default:
                className = null;
        }

        try {
            return null == className ? null : MDSClassLoader.getInstance().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    protected boolean isByteArray(Class<?> cls) {
        return Byte[].class.isAssignableFrom(cls);
    }

    protected PersistenceManagerFactory getPersistenceManagerFactory() {
        return persistenceManagerFactory;
    }

    @Autowired
    @Qualifier("persistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
