package org.motechproject.mds.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.metadata.JDOMetadata;

/**
 * Holds the current JDO metadata for Seuss. Allows reloading the metadata
 * and retrieval for modifications.
 */
@Component
public class MetadataHolder {

    private PersistenceManagerFactory persistenceManagerFactory;

    private JDOMetadata jdoMetadata;

    public JDOMetadata getJdoMetadata() {
        if (jdoMetadata == null) {
            jdoMetadata = persistenceManagerFactory.newMetadata();
        }
        return jdoMetadata;
    }

    public JDOMetadata reloadMetadata() {
        jdoMetadata = persistenceManagerFactory.newMetadata();
        return jdoMetadata;
    }

    @Autowired
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }
}
