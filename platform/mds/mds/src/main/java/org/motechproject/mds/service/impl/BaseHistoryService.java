package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.ComboboxHolder;
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
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManagerFactory;
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
        return create(clazz, src, type, null);
    }

    @Transactional
    protected <T> Object create(Class<T> clazz, Object src, EntityType type, Object reference) {
        Entity entity = allEntities.retrieveByClassName(src.getClass().getName());
        Object target;

        try {
            target = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("There was a problem with creating new instance of {}", clazz);
            throw new IllegalStateException(e);
        }

        for (Field field : entity.getFields()) {
            Object value = getValue(field, src, target, type, reference);

            if (null != value) {
                PropertyUtil.safeSetProperty(target, field.getName(), value);
            }
        }

        PropertyUtil.safeSetProperty(target, "id", null);

        return target;
    }

    protected Object getValue(Field field, Object src, Object target, EntityType type, Object reference) {
        Type fieldType = field.getType();
        ComboboxHolder holder = fieldType.isCombobox() ? new ComboboxHolder(field) : null;

        Object value = fieldType.isBlob()
                ? findService(src.getClass()).getDetachedField(src, field.getName())
                : PropertyUtil.safeGetProperty(src, field.getName());

        if (null == value) {
            return null;
        } else if (fieldType.isRelationship()) {
            value = reference != null ? reference : parseRelationshipValue(field, type, value, target);
        } else if (!TypeHelper.isPrimitive(value.getClass()) && !fieldType.isBlob()) {
            value = parseValue(target, fieldType, holder, value);
        }

        return value;
    }

    private Object parseRelationshipValue(Field field, EntityType type, Object value, Object biDirRef) {
        FieldMetadata metadata = field.getMetadata(RELATED_CLASS);
        String className = metadata.getValue();
        Object obj = value;

        Class<?> clazz = getClass(className, type);

        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            List tmp = new ArrayList();

            for (Object element : collection) {
                Object item = create(clazz, element, type, biDirRef);
                tmp.add(item);
            }

            obj = tmp;
        } else {
            obj = create(clazz, obj, type, biDirRef);
        }

        return obj;
    }

    private Object parseValue(Object target, Type fieldType, ComboboxHolder holder, Object value) {
        // the value should be from the same class loader as history object
        ClassLoader classLoader = target.getClass().getClassLoader();
        String valueAsString = value.toString();
        Object obj;

        if (null != holder && holder.isEnumList()) {
            String genericType = holder.getEnumName();
            obj = TypeHelper.parse(valueAsString, List.class.getName(), genericType, classLoader);
        } else {
            String methodParameterType = getMethodParameterType(fieldType, holder);
            obj = TypeHelper.parse(valueAsString, methodParameterType, classLoader);
        }

        return obj;
    }

    private String getMethodParameterType(Type type, ComboboxHolder holder) {
        String methodParameterType;

        if (type.isCombobox() && null != holder) {
            if (holder.isEnum()) {
                methodParameterType = holder.getEnumName();
            } else if (holder.isEnumList()) {
                methodParameterType = List.class.getName();
            } else if (holder.isStringList()) {
                methodParameterType = List.class.getName();
            } else {
                methodParameterType = String.class.getName();
            }
        } else {
            methodParameterType = type.getTypeClassName();
        }

        return methodParameterType;
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
            ClassLoader entitiesClassLoader = bundleContext.getBundle().adapt(BundleWiring.class).getClassLoader();
            return null == className ? null : entitiesClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
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
