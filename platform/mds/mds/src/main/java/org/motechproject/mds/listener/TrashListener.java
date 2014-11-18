package org.motechproject.mds.listener;

import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.ServiceUtil;
import org.motechproject.mds.service.TrashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;

/**
 * The trash listener which listens to delete events.
 * Before an object gets delete this listener will act based on the configured
 * trash mode. If the trash mode is set to using trash, it will move this object
 * to the trash bin using the {@link org.motechproject.mds.service.TrashService}.
 * If trash mode is not set and deletes are permanent, this listener will remove
 * the object history. Listener operations are executed in one transaction with
 * the actual delete.
 */
public class TrashListener extends BaseListener implements DeleteLifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(TrashListener.class);

    private TrashService trashService;
    private HistoryService historyService;

    @Override
    protected void afterContextRegistered() {
        trashService = getApplicationContext().getBean(TrashService.class);
        historyService = getApplicationContext().getBean(HistoryService.class);
    }

    @Override
    public void preDelete(InstanceLifecycleEvent event) {
        Object instance = event.getSource();
        String className = instance.getClass().getName();

        // omit events for trash and history instances
        // get the schema version from the data service
        MotechDataService dataService = ServiceUtil.getServiceFromAppContext(getApplicationContext(), className);
        Long schemaVersion = dataService.getSchemaVersion();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Moving to trash {}, schema version {}", new Object[]{instance, schemaVersion});
        }

        if (trashService.isTrashMode()) {
            trashService.moveToTrash(instance, schemaVersion, dataService.recordHistory());
        } else if (dataService.recordHistory()) {
            // remove all historical data if we are deleting this permanently
            historyService.remove(instance);
        }
    }

    @Override
    public void postDelete(InstanceLifecycleEvent event) {
    }
}
