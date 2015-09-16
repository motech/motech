package org.motechproject.mds.service.impl.history;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.util.ObjectReferenceRepository;
import org.motechproject.mds.util.PropertyUtil;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>BasePersistenceService</code> class provides utility methods for communication
 * with the database for {@link HistoryServiceImpl} and {@link TrashServiceImpl}. It allows
 * to create and retrieve instances, load proper classes and parse values.
 */
public abstract class BasePersistenceService {

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
    protected <T> Object create(Class<T> clazz, Object instance, EntityType type, ValueGetter valueGetter) {
        return create(clazz, instance, type, valueGetter, new ObjectReferenceRepository());
    }

    @Transactional
    protected <T> Object create(Class<T> clazz, Object instance, EntityType type, ValueGetter valueGetter,
                                ObjectReferenceRepository objectReferenceRepository) {
        Entity entity = allEntities.retrieveByClassName(instance.getClass().getName());
        Object recordInstance;

        try {
            recordInstance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("There was a problem with creating new instance of {}" + clazz, e);
        }

        valueGetter.updateRecordFields(recordInstance, instance);

        for (Field field : entity.getFields()) {
            // we don't generate version field for trash and history
            if (field.isVersionField()) {
                continue;
            }

            Object value = valueGetter.getValue(field, instance, recordInstance, type, objectReferenceRepository);

            if (null != value) {
                PropertyUtil.safeSetProperty(recordInstance, field.getName(), value instanceof byte[] ? ArrayUtils.toObject((byte[]) value) : value);
            }
        }

        PropertyUtil.safeSetProperty(recordInstance, "id", null);

        return recordInstance;
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
