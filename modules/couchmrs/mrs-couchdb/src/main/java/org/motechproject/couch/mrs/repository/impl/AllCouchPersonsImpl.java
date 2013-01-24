package org.motechproject.couch.mrs.repository.impl;

import java.util.Collections;
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchPersons;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllCouchPersonsImpl extends MotechBaseRepository<CouchPerson> implements AllCouchPersons {

    @Autowired
    protected AllCouchPersonsImpl(@Qualifier("couchPersonDatabaseConnector") CouchDbConnector db) {
        super(CouchPerson.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_personId", map = "function(doc) { if (doc.type ==='Person') { emit(doc.personId, doc._id); }}")
    public List<CouchPerson> findByPersonId(String personId) {
        if (personId == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_personId").key(personId).includeDocs(true);
        return db.queryView(viewQuery, CouchPerson.class);
    }

    @Override
    public void addPerson(CouchPerson person) throws MRSCouchException {

        if (person.getPersonId() == null) {
            throw new NullPointerException("Person ID cannot be null.");
        }

        List<CouchPerson> persons = findByPersonId(person.getPersonId());

        if (!persons.isEmpty()) {
            CouchPerson couchPerson = persons.get(0);
            updateFields(couchPerson, person);
            update(couchPerson);
            return;
        }

        try {
            super.add(person);
        } catch (IllegalArgumentException e) {
            throw new MRSCouchException(e.getMessage(), e);
        }
    }

    private void updateFields(CouchPerson couchPerson, CouchPerson person) {
        couchPerson.setAddress(person.getAddress());
        couchPerson.setAge(person.getAge());
        couchPerson.setAttributes(person.getAttributes());
        couchPerson.setBirthDateEstimated(person.getBirthDateEstimated());
        couchPerson.setDateOfBirth(person.getDateOfBirth());
        couchPerson.setDead(person.isDead());
        couchPerson.setDeathDate(person.getDeathDate());
        couchPerson.setFirstName(person.getFirstName());
        couchPerson.setGender(person.getGender());
        couchPerson.setLastName(person.getLastName());
        couchPerson.setMiddleName(person.getMiddleName());
        couchPerson.setPreferredName(person.getPreferredName());
    }

    @Override
    public void update(CouchPerson person) {
        super.update(person);
    }

    @Override
    public void remove(CouchPerson person) {
        super.remove(person);
    }

    @Override
    @View(name = "findAllPersons", map = "function(doc) {if (doc.type == 'Person') {emit(null, doc._id);}}")
    public List<CouchPerson> findAllPersons() {
        List<CouchPerson> ret = queryView("findAllPersons");
        if (null == ret) {
            ret = Collections.<CouchPerson> emptyList();
        }
        return ret;
    }
}
