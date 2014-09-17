package org.motechproject.mds.listener;

import org.motechproject.mds.service.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.StoreLifecycleListener;

public class HistoryListener extends BaseListener implements StoreLifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(HistoryListener.class);

    private HistoryService historyService;

    @Override
    protected void afterContextRegistered() {
        historyService = getApplicationContext().getBean(HistoryService.class);
    }

    @Override
    public void preStore(InstanceLifecycleEvent event) {
    }

    @Override
    public void postStore(InstanceLifecycleEvent event) {
        Object instance = event.getSource();

        LOG.debug("Recording history for {}", instance);

        historyService.record(instance);
    }
}
