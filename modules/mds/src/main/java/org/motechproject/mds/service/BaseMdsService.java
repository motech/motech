package org.motechproject.mds.service;

import org.motechproject.mds.PersistanceClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.jdo.PersistenceManagerFactory;

/**
 * The <code>BaseMdsService</code> class is a base class for all services in mds module.
 */
public class BaseMdsService {
    private PersistenceManagerFactory persistenceManagerFactory;
    private PersistanceClassLoader persistanceClassLoader;

    protected PersistenceManagerFactory getPersistenceManagerFactory() {
        return persistenceManagerFactory;
    }

    @Autowired
    @Qualifier("persistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    protected PersistanceClassLoader getPersistanceClassLoader() {
        return persistanceClassLoader;
    }

    @Autowired
    public void setPersistanceClassLoader(PersistanceClassLoader persistanceClassLoader) {
        this.persistanceClassLoader = persistanceClassLoader;
    }
}
