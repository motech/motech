package org.motechproject.mds.service.impl.history;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.util.ObjectReference;
import org.motechproject.mds.util.PropertyUtil;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(BasePersistenceService.class);

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
    protected <T> Object create(Class<T> clazz, Object src, EntityType type, ValueGetter valueGetter) {
        return create(clazz, src, type, valueGetter, null);
    }

    @Transactional
    protected <T> Object create(Class<T> clazz, Object src, EntityType type, ValueGetter valueGetter,
                                ObjectReference objectReference) {
        Entity entity = allEntities.retrieveByClassName(src.getClass().getName());
        Object target;

        try {
            target = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("There was a problem with creating new instance of {}", clazz);
            throw new IllegalStateException(e);
        }

        for (Field field : entity.getFields()) {
            valueGetter.updateRelationshipField(target, src);

            Object value = valueGetter.getValue(field, src, target, type, objectReference);

            if (null != value) {
                PropertyUtil.safeSetProperty(target, field.getName(), value);
            }
        }

        PropertyUtil.safeSetProperty(target, "id", null);

        return target;
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
