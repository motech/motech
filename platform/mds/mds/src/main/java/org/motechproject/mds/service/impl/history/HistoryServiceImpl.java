package org.motechproject.mds.service.impl.history;

import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.PropertyBuilder;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.QueryUtil;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.HistoryTrashClassHelper;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.List;

import static org.motechproject.mds.util.Constants.Util.ID_FIELD_NAME;

/**
 * Default implementation of {@link org.motechproject.mds.service.HistoryService} interface.
 */
public class HistoryServiceImpl extends BasePersistenceService implements HistoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryServiceImpl.class);

    private ThreadLocal<RecordRepository> recordRepositoryTL = new ThreadLocal<>();
    private ThreadLocal<Boolean> txSyncRegisteredTL = new ThreadLocal<>();

    @Override
    @Transactional
    public void record(Object instance) {
        // the history service will want to be notified once the TX completes
        // it will then clear its record repository cache
        registerPreCommitTxSync();

        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());

        if (null != historyClass) {
            LOGGER.debug("Recording history for: {}", instance);

            if (shouldRecordHistory(historyClass, instance)) {
                Long instanceId = getInstanceId(instance);
                // we can use an existing record if it was stored in this TX
                Object existingRecord = getRecordRepository().get(historyClass.getName(), instanceId);

                Object historyRecord = createRecord(historyClass, instance, existingRecord);

                getRecordRepository().store(instanceId, historyRecord);

                LOGGER.debug("Recorded history for: {}", instance);
            } else {
                LOGGER.debug("No changes for: {}, skipping", instance);
            }
        }
    }

    @Override
    @Transactional
    public List getHistoryForInstance(Object instance, QueryParams queryParams) {
        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());
        List list = new ArrayList();

        if (null != historyClass) {
            Long objId = getInstanceId(instance);

            Query query = initQuery(historyClass);
            QueryUtil.setQueryParams(query, queryParams);

            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                // We want to be sure that proper class loader will be used
                Thread.currentThread().setContextClassLoader(historyClass.getClassLoader());
                list = new ArrayList((List) query.execute(objId));
            } finally {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }

            // Remove current revision from the list of historical revisions
            list.remove(getLatestRevision(historyClass, objId));
        }

        return list;
    }

    @Override
    @Transactional
    public long countHistoryRecords(Object instance) {
        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());
        Long objId = getInstanceId(instance);

        Query query = initQuery(historyClass);
        QueryUtil.setCountResult(query);

        return (long) query.execute(objId) - 1;
    }

    @Override
    @Transactional
    public Object getSingleHistoryInstance(Object instance, Long historyId) {
        Class<?> historyClass = HistoryTrashClassHelper.getClass(instance, EntityType.HISTORY, getBundleContext());
        Object obj = null;

        if (null != historyClass) {
            Query query = initQuery(historyClass);

            List<Property> properties = new ArrayList<>();
            properties.add(PropertyBuilder.create("id", historyId, Long.class));
            QueryUtil.useFilter(query, properties);

            query.setUnique(true);

            obj = query.execute(historyId);
        }

        return obj;
    }

    private boolean shouldRecordHistory(Class<?> historyClass, Object instance) {
        // we don't want duplicate history instances
        // this checks will prevent double history being recorder from cascade events etc.
        Long instanceId = getInstanceId(instance);
        if (JDOHelper.isNew(instance)) {
            // always record for new instances, no need for db query
            return true;
        } else if (getRecordRepository().contains(historyClass.getName(), instanceId)) {
            // if it was already recorded in this tx, then we want to update it
            return true;
        } else {
            // check if there are any changes, this will prevent double history in case of cascading etc.
            Object latestHistoryRev = getLatestRevision(historyClass, instanceId);
            if (latestHistoryRev == null) {
                // no history, record data (possible in case of changing the record history setting for an entity)
                return true;
            } else {
                // check if any fields changed
                List<String> changedFields = PropertyUtil.findChangedFields(instance, latestHistoryRev, getRelConverter());
                return !changedFields.isEmpty();
            }
        }
    }

    private <T> Object createRecord(Class<T> historyClass, Object instance, Object existingRecord) {
        Object currentHistoryInstance = create(historyClass, instance, existingRecord);

        setHistoryProperties(currentHistoryInstance, instance);

        PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

        LOGGER.debug("Create a new history entry for {}", instance.getClass().getName());
        manager.makePersistent(currentHistoryInstance);

        return currentHistoryInstance;
    }

    private void setHistoryProperties(Object newHistoryObj, Object realCurrentObj) {
        // creates connection between instance object and history object
        Long id = getInstanceId(realCurrentObj);
        PropertyUtil.safeSetProperty(newHistoryObj,
                HistoryTrashClassHelper.currentVersion(newHistoryObj.getClass()), id);

        // add current entity schema version
        Long schemaVersion = getCurrentSchemaVersion(realCurrentObj.getClass().getName());
        PropertyUtil.safeSetProperty(newHistoryObj,
                HistoryTrashClassHelper.historySchemaVersion(newHistoryObj.getClass()), schemaVersion);
    }

    private Object getLatestRevision(Class<?> historyClass, Long instanceId) {
        Query query = initQuery(historyClass);
        QueryUtil.setQueryParams(query,
                new QueryParams(1, 1, new Order(ID_FIELD_NAME, Order.Direction.DESC)));
        query.setUnique(true);
        return query.execute(instanceId);
    }


    private Query initQuery(Class<?> historyClass) {
        List<Property> properties = new ArrayList<>(3);

        // we need only a correct type (not value) that's why we pass dummy values, instead of actual ones
        properties.add(PropertyBuilder.create(HistoryTrashClassHelper.currentVersion(historyClass), 1L, Long.class));

        PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

        Query query = manager.newQuery(historyClass);
        QueryUtil.useFilter(query, properties);

        return query;
    }

    private RecordRepository getRecordRepository() {
        RecordRepository repository = recordRepositoryTL.get();
        if (repository == null) {
            repository = new RecordRepository();
            recordRepositoryTL.set(repository);
        }
        return repository;
    }

    private void registerPreCommitTxSync() {
        Boolean txSyncRegistered = txSyncRegisteredTL.get();
        if (txSyncRegistered == null || !txSyncRegistered) {
            TransactionSynchronizationManager.registerSynchronization(new HistoryPersistSynchronization());
            txSyncRegisteredTL.set(true);
        }
    }

    /**
     * This TX sync does history related cleanup once a TX completes.
     * It clears the repository of the records we have stored in this TX.
     */
    private class HistoryPersistSynchronization extends TransactionSynchronizationAdapter {
        @Override
        public void afterCompletion(int status) {
            getRecordRepository().clear();
            txSyncRegisteredTL.set(false);
        }
    }
}
