package org.motechproject.mds;

import org.motechproject.mds.domain.EntityMapping;
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
import javax.jdo.Query;
import java.io.IOException;
import java.util.List;

/**
 * The purpose of this class is to create classes for all entities that are in MDS database.
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
            Query query = getPersistenceManager().newQuery(EntityMapping.class);
            List<EntityMapping> mappings = (List<EntityMapping>) query.execute();

            for (EntityMapping mapping : mappings) {
                try {
                    constructor.constructEntity(mapping);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
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
