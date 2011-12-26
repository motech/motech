package org.motechproject.openmrs.services;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.openmrs.*;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.is;

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

    public PersonName getFirstName(Set<PersonName> names) {
        return filter(having(on(PersonName.class).isPreferred(), is(false)), names).get(0);
    }


    public String getPreferredName(Set<PersonName> names) {
        final List<PersonName> preferredNames = filter(having(on(PersonName.class).isPreferred(), is(true)), names);
        if (CollectionUtils.isNotEmpty(preferredNames)) {
            return preferredNames.get(0).getGivenName();
        }
        return null;
    }

}
