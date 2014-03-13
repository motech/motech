package org.motechproject.mds;

import org.motechproject.mds.osgi.MDSApplicationContextTracker;
import org.motechproject.mds.service.JarGeneratorService;
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
    private MDSApplicationContextTracker mdsApplicationContextTracker;

    @PostConstruct
    public void constructEntities() throws IOException {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionEntityConstructor());

        mdsApplicationContextTracker.startTracker();
        LOG.info("Annotation scanner started");

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
    public void setMdsApplicationContextTracker(MDSApplicationContextTracker mdsApplicationContextTracker) {
        this.mdsApplicationContextTracker = mdsApplicationContextTracker;
    }
}
