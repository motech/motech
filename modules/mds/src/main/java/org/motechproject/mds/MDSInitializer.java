package org.motechproject.mds;

import org.motechproject.mds.service.MDSConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import java.io.IOException;

/**
 * The purpose of this class is to build classes for all entities that are in MDS database at startup.
 * It uses the {@link org.motechproject.mds.service.MDSConstructor} for generation. Since @PostConstruct does
 * not work with @Transactional, we use a {@link org.springframework.transaction.support.TransactionCallbackWithoutResult}
 * implementation.
 */
@Component
public class MDSInitializer {
    private PersistenceManagerFactory persistenceManagerFactory;
    private JdoTransactionManager transactionManager;
    private MDSConstructor constructor;

    @PostConstruct
    public void constructEntities() throws IOException {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionEntityConstructor());
    }

    private class TransactionEntityConstructor extends TransactionCallbackWithoutResult {

        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
            constructor.generateAllEntities();
        }

    }

    @Autowired
    @Qualifier("persistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    public PersistenceManager getPersistenceManager() {
        return null != persistenceManagerFactory
                ? persistenceManagerFactory.getPersistenceManager()
                : null;
    }

    @Autowired
    @Qualifier("transactionManager")
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Autowired
    public void setConstructor(MDSConstructor constructor) {
        this.constructor = constructor;
    }
}
