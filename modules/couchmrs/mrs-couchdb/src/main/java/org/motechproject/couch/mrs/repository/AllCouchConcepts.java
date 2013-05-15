package org.motechproject.couch.mrs.repository;

import org.motechproject.couch.mrs.model.CouchConcept;
import org.motechproject.couch.mrs.model.MRSCouchException;

import java.util.List;

public interface AllCouchConcepts {
    List<CouchConcept> findByConceptId(String conceptId);

    void addConcept(CouchConcept concept) throws MRSCouchException;

    void update(CouchConcept concept);

    void remove(CouchConcept concept);

    List<CouchConcept> getAll();

    List<CouchConcept> findByConceptName(String name);
}
