package org.motechproject.couch.mrs.repository.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchConcept;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchConcepts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class AllCouchConceptsImpl extends MotechBaseRepository<CouchConcept> implements AllCouchConcepts{

    @Autowired
    protected AllCouchConceptsImpl(@Qualifier("couchConceptDatabaseConnector") CouchDbConnector db) {
        super(CouchConcept.class, db);
    }

    @Override
    @View(name = "by_conceptId", map = "function(doc) { if (doc.type ==='Concept') { emit(doc._id, doc._id); }}")
    public List<CouchConcept> findByConceptId(String id) {

        if (id == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_conceptId").key(id).includeDocs(true);
        return db.queryView(viewQuery, CouchConcept.class);
    }

    @Override
    public void addConcept(CouchConcept concept) throws MRSCouchException {

        List<CouchConcept> concepts = findByConceptId(concept.getId());

        if (!concepts.isEmpty()) {
            CouchConcept couchConcept = concepts.get(0);
            couchConcept.setName(concept.getName());
            update(couchConcept);
            return;
        }

        try {
            add(concept);
        } catch (IllegalArgumentException e) {
            throw new MRSCouchException(e.getMessage(), e);
        }
    }

    @Override
    @View(name = "by_conceptName", map = "function(doc) { if (doc.type ==='Concept') { emit(doc.name.name, doc._id); }}")
    public List<CouchConcept> findByConceptName(String name) {

        if (name == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_conceptName").key(name).includeDocs(true);
        return db.queryView(viewQuery, CouchConcept.class);
    }
}
