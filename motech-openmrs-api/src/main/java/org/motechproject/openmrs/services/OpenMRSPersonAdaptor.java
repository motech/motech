package org.motechproject.openmrs.services;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPersonAdaptor;
import org.openmrs.*;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.Lambda.project;
import static org.hamcrest.Matchers.is;

public class OpenMRSPersonAdaptor implements MRSPersonAdaptor {

    private PersonService personService;


    @Autowired
    public OpenMRSPersonAdaptor(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public Integer getAgeOfAPerson(String id) {
        return getPersonById(id).getAge();
    }

    public MRSPerson openMRSToMRSPerson(Person person) {

        Set<PersonName> personNames = person.getNames();
        PersonName personName = getFirstName(personNames);

        final List<Attribute> attributes = project(person.getAttributes(), Attribute.class,
                on(PersonAttribute.class).getAttributeType().toString(), on(PersonAttribute.class).getValue());

        MRSPerson mrsPerson = new MRSPerson().firstName(personName.getGivenName()).middleName(personName.getMiddleName())
                .lastName(personName.getFamilyName()).preferredName(getPreferredName(personNames))
                .birthDateEstimated(person.getBirthdateEstimated()).gender(person.getGender())
                .address(getAddress(person)).attributes(attributes).dateOfBirth(person.getBirthdate()).dead(person.isDead());

        if (person.getId() != null) {
            mrsPerson.id(Integer.toString(person.getId()));
        }
        return mrsPerson;
    }

    private String getAddress(Person person) {
        String address = null;
        final Set<PersonAddress> addresses = person.getAddresses();
        if (!addresses.isEmpty()) {
            address = addresses.iterator().next().getAddress1();
        }
        return address;
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
