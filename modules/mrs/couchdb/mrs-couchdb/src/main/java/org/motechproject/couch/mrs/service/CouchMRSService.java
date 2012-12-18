package org.motechproject.couch.mrs.service;

import java.util.List;

import org.joda.time.DateTime;
import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.couch.mrs.model.Attribute;
import org.motechproject.couch.mrs.model.MRSCouchException;

/**
 * This service provides a means for other modules to perform CRUD operations on
 * the AllCouchMRSPersons entity in CouchDB.
 */
public interface CouchMRSService {

    void addPerson(CouchMRSPerson person) throws MRSCouchException;

    void addPerson(String externalId, String firstName, String lastName, DateTime dateOfBirth, String gender,
            String address, List<Attribute> attributes) throws MRSCouchException;

    void updatePerson(CouchMRSPerson person);

    void removePerson(CouchMRSPerson person);

    List<CouchMRSPerson> findAllCouchMRSPersons();

    List<CouchMRSPerson> findByExternalId(String externalId);

    void removeAll();

}
