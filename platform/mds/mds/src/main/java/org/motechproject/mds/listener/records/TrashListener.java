package org.motechproject.mds.listener.records;

import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.ServiceUtil;
import org.motechproject.mds.service.TrashService;

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
public class TrashListener extends BaseListener<TrashService> implements DeleteLifecycleListener {

    @Override
    public void preDelete(InstanceLifecycleEvent event) {
        Object instance = event.getSource();
        String className = instance.getClass().getName();

        getLogger().trace("Received pre-delete for: {}", instance);

        // omit events for trash and history instances
        // get the schema version from the data service
        MotechDataService dataService = ServiceUtil.getServiceFromAppContext(getApplicationContext(), className);
        Long schemaVersion = dataService.getSchemaVersion();

        if (getService().isTrashMode()) {
            getLogger().debug("Moving to trash {}, schema version {}", new Object[]{instance, schemaVersion});
            getService().moveToTrash(instance, schemaVersion);
        }
    }

    @Override
    public void postDelete(InstanceLifecycleEvent event) {
        Object instance = event.getSource();
        getLogger().trace("Received post-delete for: {}", instance);
    }

    @Override
    protected Class<TrashService> getServiceClass() {
        return TrashService.class;
    }
}
