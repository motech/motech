package org.motechproject.mds.service.impl.history.listener;

import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;

public class TrashListener extends BaseListener implements DeleteLifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(TrashListener.class);

    private TrashService trashService;

    @Override
    protected void afterContextRegistered() {
        trashService = getApplicationContext().getBean(TrashService.class);
    }

    @Override
    public void preDelete(InstanceLifecycleEvent event) {
        Object instance = event.getSource();
        String className = instance.getClass().getName();

        // omit events for trash and history instances
        // get the schema version from the data service
        MotechDataService dataService = ServiceUtil.getServiceFromAppContext(getApplicationContext(), className);
        Long schemaVersion = dataService.getSchemaVersion();

        LOG.info("Moving to trash {}, schema version {}", new Object[]{instance, schemaVersion});

        if (trashService.isTrashMode()) {
            trashService.moveToTrash(instance, schemaVersion);
        }
    }

    @Override
    public void postDelete(InstanceLifecycleEvent event) {
    }
}
