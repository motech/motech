package org.motechproject.mds.service.impl.history;

import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.RecordRelation;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.ex.ServiceNotFoundException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The <code>BaseRecordService</code> class provides utility methods for communication
 * with the database for {@link HistoryServiceImpl} and {@link TrashServiceImpl}. It allows
 * to create and retrieve instances, load proper classes and parse values.
 */
public abstract class BaseRecordService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRecordService.class);

    private PersistenceManagerFactory persistenceManagerFactory;
    private BundleContext bundleContext;
    private AllEntities allEntities;

    protected Long getEntitySchemaVersion(Object src) {
        String instanceClassName = HistoryTrashClassHelper.getInstanceClassName(src);
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

    @Transactional
    protected <T> Object create(Class<T> clazz, Object instance, EntityType type) {
        Entity entity = allEntities.retrieveByClassName(instance.getClass().getName());
        Object recordInstance;

        try {
            recordInstance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("There was a problem with creating new instance of {}", clazz);
            throw new IllegalStateException(e);
        }

        for (Field field : entity.getFields()) {
            Object value = getValue(field, instance, recordInstance, type);

            if (null != value) {
                PropertyUtil.safeSetProperty(recordInstance, field.getName(), value);
            }
        }

        PropertyUtil.safeSetProperty(recordInstance, Constants.Util.ID_FIELD_NAME, null);

        return recordInstance;
    }

    protected Object getValue(Field field, Object instance, Object recordInstance, EntityType type) {
        Type fieldType = field.getType();

        Object value = fieldType.isBlob()
                ? findService(instance.getClass()).getDetachedField(instance, field.getName())
                : PropertyUtil.safeGetProperty(instance, field.getName());

        if (null == value) {
            return null;
        } else if (fieldType.isRelationship()) {
            return RecordRelation.fromFieldValue(value);
        } else if (!TypeHelper.isPrimitive(value.getClass()) && !fieldType.isBlob()) {
            ComboboxHolder holder = fieldType.isCombobox() ? new ComboboxHolder(field) : null;
            value = parseValue(recordInstance, fieldType, holder, value);
        }

        return value;
    }

    private Object parseValue(Object target, Type fieldType, ComboboxHolder holder, Object value) {
        // the value should be from the same class loader as history object
        ClassLoader classLoader = target.getClass().getClassLoader();
        String valueAsString;

        if (value instanceof Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            valueAsString = df.format(value);
        } else {
            valueAsString = value.toString();
        }

        Object obj;

        if (null != holder && holder.isEnumList()) {
            String genericType = holder.getEnumName();
            obj = TypeHelper.parse(valueAsString, List.class.getName(), genericType, classLoader);
        } else {
            String methodParameterType = HistoryTrashClassHelper.getMethodParameterType(fieldType, holder);
            obj = TypeHelper.parse(valueAsString, methodParameterType, classLoader);
        }

        return obj;
    }


    protected MotechDataService findService(Class<?> clazz) {
        String interfaceName = MotechClassPool.getInterfaceName(clazz.getName());
        ServiceReference ref = bundleContext.getServiceReference(interfaceName);

        if (ref == null) {
            throw new ServiceNotFoundException();
        }

        return (MotechDataService) bundleContext.getService(ref);
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

    protected AllEntities getAllEntities() {
        return allEntities;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    protected BundleContext getBundleContext() {
        return bundleContext;
    }
}
