package org.motechproject.couch.mrs.repository;

import java.util.List;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;

public interface AllCouchPersons {

    List<CouchPerson> findByPersonId(String personId);

    void addPerson(CouchPerson person) throws MRSCouchException;

    void update(CouchPerson person);

    void remove(CouchPerson person);

    List<CouchPerson> findAllPersons();
}
