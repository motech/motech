package org.motechproject.mds.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.metadata.JDOMetadata;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds the current JDO metadata for Seuss. Allows reloading the metadata
 * and retrieval for modifications.
 */
@Component
public class MetadataHolder {

    private PersistenceManagerFactory persistenceManagerFactory;

    private JDOMetadata jdoMetadata;
    private Set<String> processedRelations = new HashSet<>();

    public JDOMetadata getJdoMetadata() {
        if (jdoMetadata == null) {
            jdoMetadata = persistenceManagerFactory.newMetadata();
        }
        return jdoMetadata;
    }

    public JDOMetadata reloadMetadata() {
        jdoMetadata = persistenceManagerFactory.newMetadata();
        processedRelations.clear();
        return jdoMetadata;
    }

    public boolean isRelationProcessed(String className) {
        return processedRelations.contains(className);
    }

    public void addProcessedRelation(String className) {
         processedRelations.add(className);
    }

    @Autowired
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }
}
