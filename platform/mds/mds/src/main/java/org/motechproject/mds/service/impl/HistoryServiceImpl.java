package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.PropertyBuilder;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.QueryUtil;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.motechproject.mds.util.HistoryFieldUtil.currentVersion;
import static org.motechproject.mds.util.HistoryFieldUtil.isLast;
import static org.motechproject.mds.util.HistoryFieldUtil.schemaVersion;
import static org.motechproject.mds.util.HistoryFieldUtil.trashFlag;

/**
 * Default implementation of {@link org.motechproject.mds.service.HistoryService} interface.
 */
public class HistoryServiceImpl extends BaseHistoryService implements HistoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryServiceImpl.class);

    @Override
    @Transactional
    public void record(Object instance) {
        Class<?> historyClass = getClass(instance, EntityType.HISTORY);

        if (null != historyClass) {
            LOGGER.debug("Recording history for: {}", instance.getClass().getName());

            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

            Long objId = getInstanceId(instance);
            Long schemaVersion = getEntitySchemaVersion(instance);

            Query query = initQuery(historyClass);
            query.setUnique(true);

            Object previous = query.execute(objId, true, false);
            Object current = create(historyClass, instance, EntityType.HISTORY);

            if (null == previous) {
                LOGGER.debug("Not found previous entry. Create a new history entry.");
                manager.makePersistent(current);
            } else {
                LOGGER.debug("Found previous entry.");
                PropertyUtil.safeSetProperty(previous, isLast(historyClass), false);
                PropertyUtil.safeSetProperty(current, isLast(historyClass), true);
                PropertyUtil.safeSetProperty(current, schemaVersion(historyClass), schemaVersion);

                LOGGER.debug("Create a new history entry.");
                manager.makePersistent(current);

                LOGGER.debug("Update the previous history entry.");
                manager.makePersistent(previous);
            }

            LOGGER.debug("Recorded history for: {}", instance.getClass().getName());
        }
    }

    @Override
    @Transactional
    public void remove(Object instance) {
        Class<?> historyClass = getClass(instance, EntityType.HISTORY);

        if (null != historyClass) {
            Long objId = getInstanceId(instance);

            Query query = initQuery(historyClass, false);
            query.deletePersistentAll(objId, false);
        }
    }

    @Override
    @Transactional
    public void setTrashFlag(Object instance, Object trash, boolean flag) {
        Class<?> historyClass = getClass(instance, EntityType.HISTORY);

        if (null != historyClass) {
            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
            Long objId = getInstanceId(instance);
            Long trashId = getInstanceId(trash);

            Query query = initQuery(historyClass, false, true);

            // we have to find entries with the correct instance id and trash flag that is reverse
            // to trash param.
            Collection collection = flag
                    ? (Collection) query.execute(objId, false)
                    : (Collection) query.execute(trashId, true);

            for (Object data : collection) {
                // depends on the flag param if instance object is moved to trash the history
                // entries should be connected with trash object by current version field (the same
                // is true in the opposite direction) ...
                PropertyUtil.safeSetProperty(data, currentVersion(historyClass), flag ? trashId : objId);

                // .. and the trash flag should be set (or unset).
                PropertyUtil.safeSetProperty(data, trashFlag(historyClass), flag);
            }

            // in the end all entries should be saved in database.
            manager.makePersistentAll(collection);
        }
    }

    @Override
    @Transactional
    public List getHistoryForInstance(Object instance, QueryParams queryParams) {
        Class<?> historyClass = getClass(instance, EntityType.HISTORY);
        List list = new ArrayList();

        if (null != historyClass) {
            Long objId = getInstanceId(instance);

            Query query = initQuery(historyClass, false);
            QueryUtil.setQueryParams(query, queryParams);

            list = (List) query.execute(objId, false);
        }

        return list;
    }

    @Override
    public long countHistoryRecords(Object instance) {
        Class<?> historyClass = getClass(instance, EntityType.HISTORY);
        Long objId = getInstanceId(instance);

        Query query = initQuery(historyClass, false);
        query.setResult("count(this)");

        return (long) query.execute(objId, false);
    }

    @Override
    protected <T> Object create(Class<T> clazz, Object src, EntityType type) {
        Object current = super.create(clazz, src, type);

        // creates connection between instance object and history object
        Long id = getInstanceId(src);
        PropertyUtil.safeSetProperty(current, currentVersion(clazz), id);

        // add current entity schema version
        Long schemaVersion = getEntitySchemaVersion(src);
        PropertyUtil.safeSetProperty(current, schemaVersion(clazz), schemaVersion);

        // mark as the latest revision
        PropertyUtil.safeSetProperty(current, isLast(clazz), true);

        return current;
    }

    private Query initQuery(Class<?> historyClass) {
        return initQuery(historyClass, true);
    }

    private Query initQuery(Class<?> historyClass, boolean withIsLast) {
        return initQuery(historyClass, withIsLast, true);
    }

    private Query initQuery(Class<?> historyClass, boolean withIsLast, boolean withTrashFlag) {
        List<Property> properties = new ArrayList<>(3);

        // we need only a correct type (not value) that why we pass '1L' and 'false' values
        // instead of appropriate values
        properties.add(PropertyBuilder.create(currentVersion(historyClass), 1L));

        if (withIsLast) {
            properties.add(PropertyBuilder.create(isLast(historyClass), false));
        }

        if (withTrashFlag) {
            properties.add(PropertyBuilder.create(trashFlag(historyClass), false));
        }

        PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

        Query query = manager.newQuery(historyClass);
        QueryUtil.useFilter(query, properties);

        return query;
    }

}
