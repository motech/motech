package org.motechproject.mds.repository;

import org.motechproject.commons.sql.util.Drivers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * Base for all repository classes used in Motech
 */
public abstract class AbstractRepository {

    private PersistenceManagerFactory persistenceManagerFactory;

    @Autowired
    @Qualifier("persistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    public PersistenceManagerFactory getPersistenceManagerFactory() {
        return persistenceManagerFactory;
    }

    public PersistenceManager getPersistenceManager() {
        return null != persistenceManagerFactory
                ? persistenceManagerFactory.getPersistenceManager()
                : null;
    }

    public boolean usingPsql() {
        return Drivers.POSTGRESQL_DRIVER.equals(persistenceManagerFactory.getConnectionDriverName());
    }

    protected String doubleQuote(String str) {
        return str == null ? null : '"' + str + '"';
    }
}
