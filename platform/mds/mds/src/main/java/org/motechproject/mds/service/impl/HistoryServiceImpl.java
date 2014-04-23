package org.motechproject.mds.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.InstanceUtil;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.QueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;
import java.util.List;

import static org.motechproject.mds.util.HistoryFieldUtil.currentVersion;
import static org.motechproject.mds.util.HistoryFieldUtil.isLast;
import static org.motechproject.mds.util.HistoryFieldUtil.schemaVersion;
import static org.motechproject.mds.util.HistoryFieldUtil.trashFlag;
import static org.motechproject.mds.util.QueryUtil.createDeclareParameters;
import static org.motechproject.mds.util.QueryUtil.createFilter;

/**
 * Default implementation of {@link org.motechproject.mds.service.HistoryService} interface.
 */
public class HistoryServiceImpl extends BaseMdsService implements HistoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryServiceImpl.class);

    private EntityService entityService;

    @Override
    @Transactional
    public void record(Object instance) {
        Class<?> history = getHistoryClass(instance);

        if (null != history) {
            LOGGER.debug("Recording history for: {}", instance.getClass().getName());

            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

            Long objId = InstanceUtil.getInstanceId(instance);
            Long schemaVersion = getEntitySchemaVersionForInstance(instance);

            Query query = manager.newQuery(history);
            query.setFilter(previousFilter(history));
            query.declareParameters(createDeclareParameters(objId));
            query.setUnique(true);

            Object previous = query.execute(objId);
            Object current = createCurrentHistory(history, instance);

            if (null == previous) {
                LOGGER.debug("Not found previous entry. Create a new history entry.");
                manager.makePersistent(current);
            } else {
                LOGGER.debug("Found previous entry.");
                PropertyUtil.safeSetProperty(previous, isLast(history), false);
                PropertyUtil.safeSetProperty(current, isLast(history), true);
                PropertyUtil.safeSetProperty(current, schemaVersion(history), schemaVersion);

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
        Class<?> history = getHistoryClass(instance);

        if (null != history) {
            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
            Long objId = InstanceUtil.getInstanceId(instance);

            Query query = manager.newQuery(history);
            query.setFilter(QueryUtil.createFilter(new String[]{currentVersion(history)}));
            query.declareParameters(QueryUtil.createDeclareParameters(new Object[]{objId}));
            query.deletePersistentAll(objId);
        }
    }

    @Override
    @Transactional
    public void setTrashFlag(Object instance, Object trash, boolean flag) {
        Class<?> history = getHistoryClass(instance);

        if (null != history) {
            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
            Long objId = InstanceUtil.getInstanceId(instance);
            Long trashId = InstanceUtil.getInstanceId(trash);

            Query query = manager.newQuery(history);
            query.setFilter(QueryUtil.createFilter(new String[]{currentVersion(history)}));

            Collection collection;

            if (flag) {
                query.declareParameters(QueryUtil.createDeclareParameters(new Object[]{objId}));
                collection = (Collection) query.execute(objId);
            } else {
                query.declareParameters(QueryUtil.createDeclareParameters(new Object[]{trashId}));
                collection = (Collection) query.execute(trashId);
            }

            for (Object data : collection) {
                if (flag) {
                    PropertyUtil.safeSetProperty(data, currentVersion(history), trashId);
                } else {
                    PropertyUtil.safeSetProperty(data, currentVersion(history), objId);
                }

                PropertyUtil.safeSetProperty(data, trashFlag(history), flag);

                manager.makePersistent(data);
            }
        }
    }

    @Override
    @Transactional
    public List getHistoryForInstance(Object instance) {
        Class<?> history = getHistoryClass(instance);

        if (null != history) {
            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
            Long objId = InstanceUtil.getInstanceId(instance);

            Query query = manager.newQuery(history);

            String queryFilter = QueryUtil.createFilter(new String[]{currentVersion(history)});
            queryFilter += QueryUtil.FILTER_AND + isLast(history) + " == false";

            query.setFilter(queryFilter);
            query.declareParameters(QueryUtil.createDeclareParameters(new Object[]{objId}));
            return (List) query.execute(objId);
        }

        return null;
    }

    private Class<?> getHistoryClass(Object instance) {
        String instanceClassName = InstanceUtil.getInstanceClassName(instance);
        String historyClassName = ClassName.getHistoryClassName(instanceClassName);
        Class<?> loadClass = null;

        try {
            loadClass = MDSClassLoader.getInstance().loadClass(historyClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.error(ExceptionUtils.getMessage(e));
        }

        return loadClass;
    }

    private Long getEntitySchemaVersionForInstance(Object instance) {
        String instanceClassName = InstanceUtil.getInstanceClassName(instance);
        return entityService.getCurrentSchemaVersion(instanceClassName);
    }

    private Object createCurrentHistory(Class<?> historyClass, Object instance) {
        Object current = InstanceUtil.copy(historyClass, instance, "id");
        Long schemaVersion = getEntitySchemaVersionForInstance(instance);

        // creates connection between instance object and history object
        Long id = InstanceUtil.getInstanceId(instance);
        PropertyUtil.safeSetProperty(current, currentVersion(historyClass), id);

        // add current entity schema version
        PropertyUtil.safeSetProperty(current, schemaVersion(historyClass), schemaVersion);

        // mark as the latest revision
        PropertyUtil.safeSetProperty(current, isLast(historyClass), true);


        return current;
    }

    private String previousFilter(Class<?> historyClass) {
        String filter = createFilter(currentVersion(historyClass));
        filter += QueryUtil.FILTER_AND + isLast(historyClass) + " == true";

        return filter;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

}
