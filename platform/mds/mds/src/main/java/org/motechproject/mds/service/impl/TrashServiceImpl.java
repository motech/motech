package org.motechproject.mds.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.config.DeleteMode;
import org.motechproject.mds.config.SettingsWrapper;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EmptyTrashException;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.InstanceUtil;
import org.motechproject.mds.util.QueryUtil;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.motechproject.mds.util.Constants.Config.EMPTY_TRASH_EVENT;
import static org.motechproject.mds.util.Constants.Config.EMPTY_TRASH_JOB_ID;
import static org.motechproject.mds.util.Constants.Config.MODULE_SETTINGS_CHANGE;
import static org.motechproject.scheduler.MotechSchedulerService.JOB_ID_KEY;

/**
 * Default implementation of {@link org.motechproject.mds.service.TrashService} interface.
 */
public class TrashServiceImpl extends BaseMdsService implements TrashService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrashServiceImpl.class);

    private MotechSchedulerService schedulerService;
    private SettingsWrapper settingsWrapper;
    private HistoryService historyService;
    private EntityService entityService;

    @Override
    public boolean isTrashMode() {
        return settingsWrapper.getDeleteMode() == DeleteMode.TRASH;
    }

    @Override
    @Transactional
    public void moveToTrash(Object instance) {
        Class<?> trashClass = getTrashClass(instance);

        if (null != trashClass) {
            LOGGER.debug("Moving {} to trash", instance);

            // create and save a trash instance
            LOGGER.debug("Creating trash instance for: {}", instance);

            Object trash = InstanceUtil.copy(trashClass, instance, "id");

            LOGGER.debug("Created trash instance for: {}", instance);

            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

            manager.makePersistent(trash);

            // set the flag in historical data
            historyService.setTrashFlag(instance, trash, true);
        } else {
            throw new IllegalStateException(
                    "Not found the trash class for " + instance.getClass().getName()
            );
        }
    }

    @Override
    @Transactional
    public Object findTrashById(Object instanceId, Object entityId) {
        PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
        EntityDto entity = entityService.getEntity(Long.valueOf(entityId.toString()));
        String trashClassName = ClassName.getTrashClassName(entity.getClassName());
        Object trash = null;

        try {
            Class<?> trashClass = MDSClassLoader.getInstance().loadClass(trashClassName);

            String[] properties = {"id"};
            Object[] values = {Long.valueOf(instanceId.toString())};
            Query query = manager.newQuery(trashClass);
            query.setFilter(QueryUtil.createFilter(properties, null));
            query.declareParameters(QueryUtil.createDeclareParameters(values, null));
            query.setUnique(true);
            trash = QueryUtil.executeWithArray(query, values, null);

        } catch (ClassNotFoundException e) {
            LOGGER.error("Class " + trashClassName + " not found", e);
        }

        return trash;
    }

    @Override
    @Transactional
    public void moveFromTrash(Object newInstance, Object trash) {
        PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
        historyService.setTrashFlag(newInstance, trash, false);
        manager.deletePersistent(trash);
    }

    @Override
    @MotechListener(subjects = MODULE_SETTINGS_CHANGE)
    public void scheduleEmptyTrashEvent(MotechEvent event) {
        // unchedule previous event
        schedulerService.safeUnscheduleRepeatingJob(EMPTY_TRASH_EVENT, EMPTY_TRASH_JOB_ID);

        // schedule new event only if trashMode is active and emptyTrash flag is set
        if (isTrashMode() && settingsWrapper.isEmptyTrash()) {
            Integer timeValue = settingsWrapper.getTimeValue();
            Long timeUnit = settingsWrapper.getTimeUnit().inMillis();
            long interval = timeValue * timeUnit;

            RepeatingSchedulableJob job = new RepeatingSchedulableJob(
                    createEmptyTrashEvent(), DateUtil.nowUTC().toDate(), null, interval, true
            );

            schedulerService.scheduleRepeatingJob(job);
        }
    }

    @Override
    @Transactional
    @MotechListener(subjects = EMPTY_TRASH_EVENT)
    public void emptyTrash(MotechEvent event) {
        try {
            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();

            for (EntityDto entity : entityService.listEntities()) {
                String trashClassName = ClassName.getTrashClassName(entity.getClassName());
                Class<?> trashClass = MDSClassLoader.getInstance().loadClass(trashClassName);

                Query query = manager.newQuery(trashClass);
                Collection instances = (Collection) query.execute();

                for (Object instance : instances) {
                    historyService.remove(instance);
                }

                manager.deletePersistentAll(instances);
            }
        } catch (Exception e) {
            throw new EmptyTrashException(e);
        }
    }

    @Autowired
    public void setSchedulerService(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Autowired
    public void setSettingsWrapper(SettingsWrapper settingsWrapper) {
        this.settingsWrapper = settingsWrapper;
    }

    @Autowired
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    private Class<?> getTrashClass(Object instance) {
        String instanceClassName = InstanceUtil.getInstanceClassName(instance);
        String trashClassName = ClassName.getTrashClassName(instanceClassName);
        Class<?> loadClass = null;

        try {
            loadClass = MDSClassLoader.getInstance().loadClass(trashClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.error(ExceptionUtils.getMessage(e));
        }

        return loadClass;
    }

    private MotechEvent createEmptyTrashEvent() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(JOB_ID_KEY, EMPTY_TRASH_JOB_ID);

        return new MotechEvent(EMPTY_TRASH_EVENT, parameters);
    }

}
