package org.motechproject.mds.service.impl.history;

import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.PropertyBuilder;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.QueryUtil;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.motechproject.mds.util.Constants.Util.ID_FIELD_NAME;

/**
 * Default implementation of {@link org.motechproject.mds.service.HistoryService} interface.
 */
public class HistoryServiceImpl extends BaseRecordService implements HistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryServiceImpl.class);

    @Override
    @Transactional
    public void record(Object instance) {
        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());

        if (null != historyClass) {
            LOGGER.debug("Recording history for: {}", instance.getClass().getName());

            Object record = create(historyClass, instance, EntityType.HISTORY);
            setHistoryProperties(record, instance);

            LOGGER.debug("Recording a new history entry for {}", instance.getClass().getName());
            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
            manager.makePersistent(record);
            LOGGER.debug("Recorded history for: {}", instance.getClass().getName());
        }
    }

    @Override
    @Transactional
    public void remove(Object instance) {
        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());

        if (null != historyClass) {
            Long objId = getInstanceId(instance);

            Query query = initQuery(historyClass);
            query.deletePersistentAll(objId, false);
        }
    }

    @Override
    @Transactional
    public void setTrashFlag(Object instance, Object trash, boolean flag) {
        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());

        if (null != historyClass) {
            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
            Long objId = getInstanceId(instance);
            Long trashId = getInstanceId(trash);

            Query query = initQuery(historyClass, true);

            // we have to find entries with the correct instance id and trash flag that is reverse
            // to trash param.
            Collection collection = flag
                    ? (Collection) query.execute(objId, false)
                    : (Collection) query.execute(trashId, true);

            for (Object data : collection) {
                // depends on the flag param if instance object is moved to trash the history
                // entries should be connected with trash object by current version field (the same
                // is true in the opposite direction) ...
                PropertyUtil.safeSetProperty(data, HistoryTrashClassHelper.currentVersion(historyClass),
                        flag ? trashId : objId);

                // .. and the trash flag should be set (or unset).
                PropertyUtil.safeSetProperty(data, HistoryTrashClassHelper.trashFlag(historyClass), flag);
            }

            // in the end all entries should be saved in database.
            manager.makePersistentAll(collection);
        }
    }

    @Override
    @Transactional
    public List getHistoryForInstance(Object instance, QueryParams queryParams) {
        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());
        List list = new ArrayList();

        if (null != historyClass) {
            Long objId = getInstanceId(instance);

            Query query = initQuery(historyClass, false);
            QueryUtil.setQueryParams(query, queryParams);

            list = new ArrayList((List) query.execute(objId));
            // Remove current revision from the list of historical revisions
            list.remove(getLatestRevision(historyClass, objId));
        }

        return list;
    }

    @Override
    public long countHistoryRecords(Object instance) {
        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());
        Long objId = getInstanceId(instance);

        Query query = initQuery(historyClass, false);
        query.setResult("count(this)");

        return (long) query.execute(objId) - 1;
    }

    @Transactional
    public Object getSingleHistoryInstance(Object instance, Long historyId) {
        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());
        Object obj = null;

        if (null != historyClass) {
            Query query = initQuery(historyClass, false);

            List<Property> properties = new ArrayList<>();
            properties.add(PropertyBuilder.create("id", historyId));
            QueryUtil.useFilter(query, properties);

            query.setUnique(true);

            obj = query.execute(historyId);
        }

        return obj;
    }

    private void setHistoryProperties(Object newHistoryObj, Object realCurrentObj) {
        // creates connection between instance object and history object
        Long id = getInstanceId(realCurrentObj);
        PropertyUtil.safeSetProperty(newHistoryObj,
                HistoryTrashClassHelper.currentVersion(newHistoryObj.getClass()), id);

        // add current entity schema version
        Long schemaVersion = getEntitySchemaVersion(realCurrentObj);
        PropertyUtil.safeSetProperty(newHistoryObj,
                HistoryTrashClassHelper.schemaVersion(newHistoryObj.getClass()), schemaVersion);
    }

    private Object getLatestRevision(Class<?> historyClass, Long instanceId) {
        Query query = initQuery(historyClass, false);
        QueryUtil.setQueryParams(query,
                new QueryParams(1, 1, new Order(ID_FIELD_NAME, Order.Direction.DESC)));
        query.setUnique(true);
        return query.execute(instanceId);
    }

    private Query initQuery(Class<?> historyClass) {
        return initQuery(historyClass, true);
    }

    private Query initQuery(Class<?> historyClass, boolean withTrashFlag) {
        List<Property> properties = new ArrayList<>(3);

        // we need only a correct type (not value) that's why we pass dummy values, instead of actual ones
        properties.add(PropertyBuilder.create(HistoryTrashClassHelper.currentVersion(historyClass), 1L));

        if (withTrashFlag) {
            properties.add(PropertyBuilder.create(HistoryTrashClassHelper.trashFlag(historyClass), false));
        }

        PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

        Query query = manager.newQuery(historyClass);
        QueryUtil.useFilter(query, properties);

        return query;
    }
}
