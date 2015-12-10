package org.motechproject.mds.listener.records;

import org.motechproject.mds.service.HistoryService;

import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.StoreLifecycleListener;

/**
 * The history listener which listens to store events.
 * After the object gets stored this listener will create its history
 * using the {@link org.motechproject.mds.service.HistoryService}. Listener
 * operations are executed in one transaction with the actual store.
 */
public class HistoryListener extends BaseListener implements StoreLifecycleListener {

    private HistoryService historyService;

    @Override
    protected void afterContextRegistered() {
        historyService = getApplicationContext().getBean(HistoryService.class);
    }

    @Override
    public void preStore(InstanceLifecycleEvent event) {
        Object instance = event.getSource();
        getLogger().trace("Pre-store event received for {}", instance);
    }

    @Override
    public void postStore(InstanceLifecycleEvent event) {
        Object instance = event.getSource();
        getLogger().trace("Post-store event received for {}", instance);

        getLogger().debug("Recording history for {}", instance);
        historyService.record(instance);
    }
}
