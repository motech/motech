package org.motechproject.mds.service.impl.history;

import org.datanucleus.enhancer.Persistable;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.ServiceUtil;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManagerFactory;
import java.beans.PropertyDescriptor;
import java.util.Collection;

/**
 * The <code>BasePersistenceService</code> class provides utility methods for communication
 * with the database for {@link HistoryServiceImpl} and {@link TrashServiceImpl}. It allows
 * to create and retrieve instances, load proper classes and parse values.
 */
public abstract class BasePersistenceService {

    private PersistenceManagerFactory persistenceManagerFactory;
    private BundleContext bundleContext;
    private ApplicationContext appContext;

    private final RelationshipConverter relConverter = new RelationshipConverter();

    protected Long getInstanceId(Object instance) {
        Object value = PropertyUtil.safeGetProperty(instance, Constants.Util.ID_FIELD_NAME);
        Number id = null;

        if (value instanceof Number) {
            id = (Number) value;
        }

        return null == id ? null : id.longValue();
    }

    @Transactional
    protected <T> Object create(Class<T> clazz, Object instance, Object existingRecord) {
        Object recordInstance = existingRecord;

        if (recordInstance == null) {
            try {
                recordInstance = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        PropertyUtil.copyProperties(recordInstance, instance, relConverter);
        // the regular copy ignores auto generated fields, we want to copy a subset of them
        PropertyUtil.copyProperties(recordInstance, instance, relConverter, Constants.Util.RECORD_FIELDS_TO_COPY);

        return recordInstance;
    }

    protected Collection<Long> convertToIdsCollection(Collection collection) {
        Collection<Long> idColl = TypeHelper.suggestAndCreateCollectionImplementation(collection.getClass());

        for (Object obj : collection) {
            idColl.add((Long) PropertyUtil.safeGetProperty(obj, Constants.Util.ID_FIELD_NAME));
        }

        return idColl;
    }

    protected Long getCurrentSchemaVersion(String className) {
        MotechDataService dataService = ServiceUtil.getServiceFromAppContext(appContext, className);
        if (dataService == null) {
            throw new IllegalStateException("Unable to retrieve data service for entity class " + className);
        }
        return dataService.getSchemaVersion();
    }

    protected PersistenceManagerFactory getPersistenceManagerFactory() {
        return persistenceManagerFactory;
    }

    protected RelationshipConverter getRelConverter() {
        return relConverter;
    }

    @Autowired
    @Qualifier("persistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    protected BundleContext getBundleContext() {
        return bundleContext;
    }

    /**
     * An implementation of {@link org.motechproject.mds.util.PropertyUtil.ValueConverter} that will
     * convert relationship fields into either ids or collections of ids (depending on the relationship type).
     * Use when copying properties.
     */
    private class RelationshipConverter implements PropertyUtil.ValueConverter {
        @Override
        public Object convert(Object value, PropertyDescriptor descriptor) {
            if (value instanceof Collection) {
                Collection coll = (Collection) value;

                if (!coll.isEmpty() && coll.iterator().next() instanceof Persistable) {
                    // relationship collection, convert to IDs
                    return convertToIdsCollection(coll);
                } else {
                    // collection of enum or regular objects
                    // copy to a new collection object
                    Collection collCopy = TypeHelper.suggestAndCreateCollectionImplementation(coll.getClass());
                    collCopy.addAll(coll);
                    return collCopy;
                }
            } else if (value instanceof Persistable) {
                // 1:1 or M:1 relationship, just copy the id
                return PropertyUtil.safeGetProperty(value, Constants.Util.ID_FIELD_NAME);
            } else {
                // regular field
                return value;
            }
        }
    }
}
