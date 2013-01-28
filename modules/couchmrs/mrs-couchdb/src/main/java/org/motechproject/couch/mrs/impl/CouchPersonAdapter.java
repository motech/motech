package org.motechproject.couch.mrs.impl;

import java.util.List;
import org.joda.time.DateTime;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchPersons;
import org.motechproject.couch.mrs.repository.impl.AllCouchPersonsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.services.PersonAdapter;

@Service
public class CouchPersonAdapter implements PersonAdapter {

    @Autowired
    private AllCouchPersons allCouchMRSPersons;

    @Override
    public void addPerson(String personId, String firstName, String lastName, DateTime dateOfBirth, String gender,
            String address, List<Attribute> attributes) throws MRSCouchException {
        CouchPerson person = new CouchPerson();
        person.setPersonId(personId);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(dateOfBirth);
        person.setGender(gender);
        person.setAddress(address);
        person.setAttributes(attributes);
        allCouchMRSPersons.addPerson(person);
    }

    @Override
    public void addPerson(Person person) throws MRSCouchException {
        allCouchMRSPersons.addPerson((CouchPerson) person);
    }

    @Override
    public void updatePerson(Person person) {
        allCouchMRSPersons.update((CouchPerson) person);
    }

    @Override
    public void removePerson(Person person) {
        allCouchMRSPersons.remove((CouchPerson) person);

    }

    @Override
    public List<CouchPerson> findAllPersons() {
        return allCouchMRSPersons.findAllPersons();
    }

    @Override
    public List<CouchPerson> findByPersonId(String personId) {
        return allCouchMRSPersons.findByPersonId(personId);
    }

    @Override
    public void removeAll() {
        ((AllCouchPersonsImpl) allCouchMRSPersons).removeAll();
    }
}
