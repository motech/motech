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

    /**
     * Simple util method that determines the database in use, based on the chosen connection driver.
     * @return true, if the driver in use belongs to PostgreSQL, false otherwise
     */
    public boolean usingPsql() {
        return Drivers.POSTGRESQL_DRIVER.equals(persistenceManagerFactory.getConnectionDriverName());
    }

    /**
     * Simple util method that wraps the given {@link String} in double quotes.
     * @param str the text to wrap in double quotes
     * @return the text wrapped in double quotes
     */
    protected String doubleQuote(String str) {
        return str == null ? null : '"' + str + '"';
    }
}
