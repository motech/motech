package org.motechproject.couch.mrs.repository;

import java.util.List;

import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;

public interface AllCouchMRSPersons {

    List<CouchMRSPerson> findByExternalId(String externalId);

    void addPerson(CouchMRSPerson person) throws MRSCouchException;

    void update(CouchMRSPerson person);

    void remove(CouchMRSPerson person);

    List<CouchMRSPerson> findAllPersons();
}
