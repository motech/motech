package org.motechproject.openmrs.services;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;
import static org.hamcrest.Matchers.is;

@Service
public class OpenMRSPersonAdapter {

    private PersonService personService;


    @Autowired
    public OpenMRSPersonAdapter(PersonService personService) {
        this.personService = personService;
    }

    OpenMRSPerson openMRSToMRSPerson(Person person) {

        Set<PersonName> personNames = person.getNames();
        PersonName personName = getFirstName(personNames);

        final List<OpenMRSAttribute> attributes = project(person.getAttributes(), OpenMRSAttribute.class,
                on(PersonAttribute.class).getAttributeType().toString(), on(PersonAttribute.class).getValue());

        List<Attribute> personAttributes = new ArrayList<Attribute>();

        personAttributes.addAll(attributes);

        OpenMRSPerson mrsPerson = new OpenMRSPerson().firstName(personName.getGivenName()).middleName(personName.getMiddleName())
                .lastName(personName.getFamilyName()).birthDateEstimated(person.getBirthdateEstimated()).gender(person.getGender()).age(person.getAge())
                .address(getAddress(person)).attributes(personAttributes).dateOfBirth(new DateTime(person.getBirthdate())).dead(person.isDead()).deathDate(new DateTime(person.getDeathDate()));

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

    Person getPersonById(String id) {
        return personService.getPerson(Integer.valueOf(id));
    }

    PersonName getFirstName(Set<PersonName> names) {
        List<PersonName> personNames = filter(having(on(PersonName.class).isPreferred(), is(false)), names);
        if (CollectionUtils.isEmpty(personNames)) {
            personNames = filter(having(on(PersonName.class).isPreferred(), is(true)), names);
        }
        return personNames.get(0);
    }
}
