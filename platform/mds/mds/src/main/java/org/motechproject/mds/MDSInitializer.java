package org.motechproject.mds;

import org.motechproject.mds.osgi.MdsBundleWatcher;
import org.motechproject.mds.osgi.MdsWeavingHook;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.server.osgi.PlatformConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;

/**
 * The purpose of this class is to build classes for all entities that are in MDS database at startup.
 * It uses the {@link org.motechproject.mds.builder.MDSConstructor} for generation. Since @PostConstruct does
 * not work with @Transactional, we use a {@link org.springframework.transaction.support.TransactionCallbackWithoutResult}
 * implementation.
 */
@Component
public class MDSInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(MDSInitializer.class);

    private JdoTransactionManager transactionManager;
    private JarGeneratorService jarGeneratorService;
    private MdsBundleWatcher mdsBundleWatcher;
    private BundleContext bundleContext;
    private MdsWeavingHook mdsWeavingHook;
    private EventAdmin eventAdmin;

    @PostConstruct
    public void initMDS() throws IOException {
        // First register the weaving hook
        bundleContext.registerService(WeavingHook.class.getName(), mdsWeavingHook, null);
        LOG.info("MDS weaving hook registered");

        // create initial entities
        try {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.execute(new TransactionEntityConstructor());
        } catch (Exception e) {
            LOG.error("Error during initial entity creation", e);
        }

        // start the bundle watcher
        try {
            mdsBundleWatcher.start();
            LOG.info("Annotation scanner started");
        } catch (Exception e) {
            LOG.error("Error while starting MDS Annotation Processor", e);
        }

        // signal that the startup can commence
        eventAdmin.postEvent(new Event(PlatformConstants.MDS_STARTUP_TOPIC, new HashMap<String, Object>()));

        LOG.info("Motech data services initialization complete");
    }

    private class TransactionEntityConstructor extends TransactionCallbackWithoutResult {

        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
            // don't build DDEs, they will be loaded when their module contexts become available
            jarGeneratorService.regenerateMdsDataBundle(false);
            LOG.info("Initial entities bundle generated");
        }
    }

    @Autowired
    @Qualifier("transactionManager")
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Autowired
    public void setJarGeneratorService(JarGeneratorService jarGeneratorService) {
        this.jarGeneratorService = jarGeneratorService;
    }

    @Autowired
    public void setMdsBundleWatcher(MdsBundleWatcher mdsBundleWatcher) {
        this.mdsBundleWatcher = mdsBundleWatcher;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setMdsWeavingHook(MdsWeavingHook mdsWeavingHook) {
        this.mdsWeavingHook = mdsWeavingHook;
    }

    @Autowired
    public void setEventAdmin(EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }
}
