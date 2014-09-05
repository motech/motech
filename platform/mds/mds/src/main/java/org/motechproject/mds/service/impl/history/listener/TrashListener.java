package org.motechproject.mds.service.impl.history.listener;

import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;

public class TrashListener extends BaseListener implements DeleteLifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(TrashListener.class);

    @Autowired
    private TrashService trashService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("persistenceManagerFactory")
    private PersistenceManagerFactory pmf;

    @PostConstruct
    public void init() {
        pmf.addInstanceLifecycleListener(this, entityClasses());
    }

    @Override
    public void preDelete(InstanceLifecycleEvent event) {
    }

    @Override
    public void postDelete(InstanceLifecycleEvent event) {
        Object instance = event.getSource();
        String className = instance.getClass().getName();

        // omit events for trash and history instances
        // get the schema version from the data service
        MotechDataService dataService = ServiceUtil.getServiceFromAppContext(applicationContext, className);
        Long schemaVersion = dataService.getSchemaVersion();

        LOG.info("Moving to trash {} {}", new Object[]{instance, schemaVersion});

        //trashService.moveToTrash(instance, schemaVersion);
    }

    @Override
    protected String getName() {
        return "TRASH";
    }
}
