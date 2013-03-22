package org.motechproject.couch.mrs.impl;

import org.joda.time.DateTime;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchPersons;
import org.motechproject.couch.mrs.repository.impl.AllCouchPersonsImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.services.MRSPersonAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouchPersonAdapter implements MRSPersonAdapter {

    @Autowired
    private AllCouchPersons allCouchMRSPersons;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public void addPerson(String personId, String firstName, String lastName, DateTime dateOfBirth, String gender,
            String address, List<MRSAttribute> attributes) throws MRSCouchException {
        CouchPerson person = new CouchPerson();
        person.setPersonId(personId);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(dateOfBirth);
        person.setGender(gender);
        person.setAddress(address);
        person.setAttributes(attributes);
        allCouchMRSPersons.addPerson(person);
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventHelper.personParameters(person)));
    }

    @Override
    public void addPerson(MRSPerson person) throws MRSCouchException {
        allCouchMRSPersons.addPerson((CouchPerson) person);
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventHelper.personParameters(person)));
    }

    @Override
    public void updatePerson(MRSPerson person) {
        allCouchMRSPersons.update((CouchPerson) person);
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_PERSON_SUBJECT, EventHelper.personParameters(person)));
    }

    @Override
    public void removePerson(MRSPerson person) {
        allCouchMRSPersons.remove((CouchPerson) person);
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_PERSON_SUBJECT, EventHelper.personParameters(person)));
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
