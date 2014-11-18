package org.motechproject.mds.listener;

import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.StoreLifecycleListener;

/**
 * The history listener which listens to store events.
 * After the object gets stored this listener will create its history
 * using the {@link org.motechproject.mds.service.HistoryService}. Listener
 * operations are executed in one transaction with the actual store.
 */
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
        String className = instance.getClass().getName();

        MotechDataService dataService = ServiceUtil.getServiceFromAppContext(getApplicationContext(), className);

        LOG.debug("Recording history for {}", instance);

        if (dataService.recordHistory()) {
            historyService.record(instance);
        }
    }
}
