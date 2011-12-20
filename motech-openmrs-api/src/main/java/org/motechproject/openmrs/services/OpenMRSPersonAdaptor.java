package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.openmrs.*;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSPersonAdaptor {

    private PersonService personService;

    @Autowired
    public OpenMRSPersonAdaptor(PersonService personService) {
        this.personService = personService;
    }

    public MRSPerson openMRSToMRSPerson(Person person) {
        PersonName personName = person.getPersonName();
        String givenName = personName.getGivenName();

        String address = person.getPersonAddress() != null ? person.getPersonAddress().getAddress1() : null;
        MRSPerson mrsPerson = new MRSPerson().firstName(givenName).middleName(personName.getMiddleName())
                .lastName(personName.getFamilyName()).address(address).
                        dateOfBirth(person.getBirthdate()).birthDateEstimated(person.getBirthdateEstimated()).gender(person.getGender()).
                        id(Integer.toString(person.getId()));
        if (personName.getPreferred()) {
            mrsPerson.preferredName(givenName);
        }
        for (PersonAttribute personAttribute : person.getAttributes()) {
            mrsPerson.addAttribute(new Attribute(personAttribute.getAttributeType().getName(), personAttribute.getValue()));
        }
        return mrsPerson;
    }

    public Person getPersonById(String id) {
        return personService.getPerson(Integer.valueOf(id));
    }
}
