package org.motechproject.mds.service.impl.history;

import com.google.common.collect.ImmutableSet;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.ServiceUtil;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.spi.PersistenceCapable;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * The <code>BasePersistenceService</code> class provides utility methods for communication
 * with the database for {@link HistoryServiceImpl} and {@link TrashServiceImpl}. It allows
 * to create and retrieve instances, load proper classes and parse values.
 */
public abstract class BasePersistenceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasePersistenceService.class);

    private static final Set<String> ADDITIONAL_FIELDS_TO_COPY = ImmutableSet.copyOf(Arrays.asList(
        Constants.Util.MODIFICATION_DATE_FIELD_NAME, Constants.Util.CREATION_DATE_FIELD_NAME,
        Constants.Util.MODIFIED_BY_FIELD_NAME, Constants.Util.CREATOR_FIELD_NAME,
        Constants.Util.OWNER_FIELD_NAME
    ));

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
                LOGGER.error("There was a problem with creating new instance of {}", clazz);
                throw new IllegalStateException(e);
            }
        }

        PropertyUtil.copyProperties(recordInstance, instance, relConverter);
        // the regular copy ignores auto generated fields, we want to copy a subset of them
        PropertyUtil.copyProperties(recordInstance, instance, relConverter, ADDITIONAL_FIELDS_TO_COPY);

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

                if (!coll.isEmpty() && coll.iterator().next() instanceof PersistenceCapable) {
                    // relationship collection, convert to IDs
                    return convertToIdsCollection(coll);
                } else {
                    // collection of enum or regular objects
                    // copy to a new collection object
                    Collection collCopy = TypeHelper.suggestAndCreateCollectionImplementation(coll.getClass());
                    collCopy.addAll(coll);
                    return collCopy;
                }
            } else if (value instanceof PersistenceCapable) {
                // 1:1 or M:1 relationship, just copy the id
                return PropertyUtil.safeGetProperty(value, Constants.Util.ID_FIELD_NAME);
            } else {
                // regular field
                return value;
            }
        }
    }
}
