package org.motechproject.openmrs.services;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.mrs.services.MRSPersonAdapter;
import org.motechproject.openmrs.helper.PersonHelper;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OpenMRSPersonAdapter implements MRSPersonAdapter {

    @Autowired
    private PersonService personService;

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    public OpenMRSPersonAdapter(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public OpenMRSPerson addPerson(MRSPerson person) {
        if (!findByPersonId(person.getPersonId()).isEmpty()) {
            return updatePerson(person);
        } else {
            Person savedPerson = personService.savePerson(PersonHelper.createPerson(person, personService.getAllPersonAttributeTypes(false)));
            OpenMRSPerson returnedPerson = PersonHelper.openMRSToMRSPerson(savedPerson);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventHelper.personParameters(returnedPerson)));
            return returnedPerson;
        }
    }

    @Override
    public OpenMRSPerson addPerson(String personId, String firstName, String lastName, DateTime dateOfBirth, String gender, String address, List<MRSAttribute> attributes) {
        OpenMRSPerson person = new OpenMRSPerson();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(dateOfBirth);
        person.setGender(gender);
        person.setAddress(address);
        person.setAttributes(attributes);
        person.setPersonId(personId);

        if (!findByPersonId(person.getPersonId()).isEmpty()) {
            return updatePerson(person);
        } else {
            Person openMrsPerson = personService.savePerson(PersonHelper.createPerson(person, personService.getAllPersonAttributeTypes(false)));
            OpenMRSPerson returnedPerson = PersonHelper.openMRSToMRSPerson(openMrsPerson);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventHelper.personParameters(returnedPerson)));
            return returnedPerson;
    }
}

    @Override
    public OpenMRSPerson updatePerson(MRSPerson person) {
        if (!findByPersonId(person.getPersonId()).isEmpty() && validatePersonId(person.getPersonId())) {
            Person personToUpdate = findByPersonIdAndReturnPerson(person.getPersonId());
            PersonHelper.updatePersonName(personToUpdate, person);
            personToUpdate.setBirthdate(person.getDateOfBirth().toDate());
            personToUpdate.setBirthdateEstimated(person.getBirthDateEstimated());
            personToUpdate.setGender(person.getGender());

            for (MRSAttribute attribute : person.getAttributes()) {
                PersonAttribute personAttribute = personToUpdate.getAttribute(attribute.getName());
                if (personAttribute != null) {
                    personToUpdate.removeAttribute(personAttribute);
                }
                personToUpdate.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(attribute.getName()), attribute.getValue()));
            }
            Set<PersonAddress> addresses = personToUpdate.getAddresses();
            if (!addresses.isEmpty()) {
                PersonAddress address = addresses.iterator().next();
                address.setAddress1(person.getAddress());
            } else {
                final String address = person.getAddress();
                PersonAddress personAddress = new PersonAddress();
                personAddress.setAddress1(address);
                personToUpdate.addAddress(personAddress);
            }
            OpenMRSPerson returnedPerson = PersonHelper.openMRSToMRSPerson(personService.savePerson(personToUpdate));
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_PERSON_SUBJECT, EventHelper.personParameters(person)));
            return returnedPerson;
        } else {
            return null;
        }
    }

    @Override
    public void removePerson(MRSPerson person) {
        Person existingPerson = personService.getPersonByUuid(person.getPersonId());
        if (existingPerson == null) {
            existingPerson = getPersonById(person.getPersonId());
        }

        if (existingPerson != null) {
            personService.purgePerson(existingPerson);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_PERSON_SUBJECT, EventHelper.personParameters(person)));
        }
    }

    @Override
    public List<OpenMRSPerson> findAllPersons() {
        List<OpenMRSPerson> mrsPersonList = new ArrayList<>();
        List<Person> personList = personService.getPeople("", null);
        for (Person person : personList) {
            mrsPersonList.add(PersonHelper.openMRSToMRSPerson(person));
        }
        return mrsPersonList;
    }

    @Override
    public List<OpenMRSPerson> findByPersonId(String personId) {
        List<OpenMRSPerson> personList = new ArrayList<>();

        Person person = personService.getPersonByUuid(personId);
        if (person != null) {
            personList.add(PersonHelper.openMRSToMRSPerson(person));
        } else {
            person = getPersonById(personId);
            if (person != null) {
                personList.add(PersonHelper.openMRSToMRSPerson(person));
            }
        }
        return personList;
    }

    private Person findByPersonIdAndReturnPerson(String personId) {
        Person person = personService.getPersonByUuid(personId);
        if (person != null) {
            return person;
        } else {
            person = getPersonById(personId);
            if (person != null) {
                return person;
            }
        }
        return null;
    }

    @Override
    public void removeAll() {
        List<Person> personList = personService.getPeople("", null);
        for (Person person : personList) {
            if (person.getPersonId() != 1) {   //You can't delete first person in db
                personService.purgePerson(person);
            }

        }
    }

    Person getPersonById(String id) {
        try {
            if (!validatePersonId(id)) {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return personService.getPerson(Integer.valueOf(id));
    }

    private boolean validatePersonId(String personId) {
        if (Integer.valueOf(personId) == 1) { //You can't delete or update first person in db
            return false;
        } else {
            return true;
        }
    }
}