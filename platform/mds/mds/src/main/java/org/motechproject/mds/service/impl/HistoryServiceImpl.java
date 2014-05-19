package org.motechproject.mds.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.instance.InstanceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        Class<?> historyClass = getHistoryClass(instance);

        if (null != historyClass) {
            LOGGER.debug("Recording history for: {}", instance.getClass().getName());

            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

            Long objId = InstanceUtil.getInstanceId(instance);
            Long schemaVersion = getEntitySchemaVersionForInstance(instance);

            Query query = initQuery(historyClass);
            query.setUnique(true);

            Object previous = query.execute(objId, true, false);
            Object current = createCurrentHistory(historyClass, instance);

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
        Class<?> historyClass = getHistoryClass(instance);

        if (null != historyClass) {
            Long objId = InstanceUtil.getInstanceId(instance);

            Query query = initQuery(historyClass, false);
            query.deletePersistentAll(objId, false);
        }
    }

    @Override
    @Transactional
    public void setTrashFlag(Object instance, Object trash, boolean flag) {
        Class<?> historyClass = getHistoryClass(instance);

        if (null != historyClass) {
            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
            Long objId = InstanceUtil.getInstanceId(instance);
            Long trashId = InstanceUtil.getInstanceId(trash);

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
    public List getHistoryForInstance(Object instance) {
        Class<?> historyClass = getHistoryClass(instance);
        List list = new ArrayList();

        if (null != historyClass) {
            Long objId = InstanceUtil.getInstanceId(instance);

            Query query = initQuery(historyClass, false);
            list = (List) query.execute(objId, false);
        }

        return list;
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

        Object current = InstanceUtil.copy(entityService.getEntityByClassName(instance.getClass().getName()), historyClass, instance, "id");

        // creates connection between instance object and history object
        Long id = InstanceUtil.getInstanceId(instance);
        PropertyUtil.safeSetProperty(current, currentVersion(historyClass), id);

        // add current entity schema version
        Long schemaVersion = getEntitySchemaVersionForInstance(instance);
        PropertyUtil.safeSetProperty(current, schemaVersion(historyClass), schemaVersion);

        // mark as the latest revision
        PropertyUtil.safeSetProperty(current, isLast(historyClass), true);


        return current;
    }

    private Query initQuery(Class<?> historyClass) {
        return initQuery(historyClass, true);
    }

    private Query initQuery(Class<?> historyClass, boolean withIsLast) {
        return initQuery(historyClass, withIsLast, true);
    }

    private Query initQuery(Class<?> historyClass, boolean withIsLast, boolean withTrashFlag) {
        List<String> fields = new ArrayList<>(3);
        List<Object> values = new ArrayList<>(3);

        fields.add(currentVersion(historyClass));
        values.add(1L);

        if (withIsLast) {
            fields.add(isLast(historyClass));
            values.add(false);
        }

        if (withTrashFlag) {
            fields.add(trashFlag(historyClass));
            values.add(false);
        }


        String filter = createFilter(fields.toArray(new String[fields.size()]));
        // we need only a correct type (not value) that why we pass '1L' and 'false' values
        // instead of appropriate values
        String declareParameters = createDeclareParameters(values.toArray(new Object[values.size()]));

        PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

        Query query = manager.newQuery(historyClass);
        query.setFilter(filter);
        query.declareParameters(declareParameters);

        return query;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

}
