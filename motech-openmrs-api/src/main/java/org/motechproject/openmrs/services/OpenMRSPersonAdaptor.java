package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;

public class OpenMRSPersonAdaptor {

    public MRSPerson convertOpenMRSToMRSPerson(Person person) {
        PersonName personName = person.getPersonName();
        String givenName = personName.getGivenName();

        MRSPerson mrsPerson = new MRSPerson().firstName(givenName).middleName(personName.getMiddleName())
                            .lastName(personName.getFamilyName()).address(person.getPersonAddress().getAddress1()).
                                dateOfBirth(person.getBirthdate()).birthDateEstimated(person.getBirthdateEstimated()).gender(person.getGender()).
                                id(Integer.toString(person.getId()));
        if(personName.getPreferred()){
            mrsPerson.preferredName(givenName);
        }
        for (PersonAttribute personAttribute : person.getAttributes()) {
            mrsPerson.addAttribute(new Attribute(personAttribute.getAttributeType().getName(), personAttribute.getValue()));
        }

        return  mrsPerson;
    }
}
