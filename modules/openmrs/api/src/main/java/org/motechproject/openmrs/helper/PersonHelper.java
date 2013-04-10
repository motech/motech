package org.motechproject.openmrs.helper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.openmrs.model.OpenMRSAttribute;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;
import static ch.lambdaj.Lambda.selectUnique;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Component
public final class PersonHelper {
    private PersonHelper() {
        // static utility class
    }

    public static OpenMRSPerson openMRSToMRSPerson(Person person) {
        Set<PersonName> personNames = person.getNames();
        PersonName personName = getFirstName(personNames);

        final List<OpenMRSAttribute> attributes = project(person.getAttributes(), OpenMRSAttribute.class,
                on(PersonAttribute.class).getAttributeType().toString(), on(PersonAttribute.class).getValue());

        List<MRSAttribute> personAttributes = new ArrayList<MRSAttribute>();

        personAttributes.addAll(attributes);

        OpenMRSPerson mrsPerson = new OpenMRSPerson().firstName(personName.getGivenName()).middleName(personName.getMiddleName())
                .lastName(personName.getFamilyName()).birthDateEstimated(person.getBirthdateEstimated()).gender(person.getGender()).age(person.getAge())
                .address(getAddress(person)).attributes(personAttributes).dateOfBirth(new DateTime(person.getBirthdate())).dead(person.isDead()).deathDate(new DateTime(person.getDeathDate()));

        if (person.getId() != null) {
            mrsPerson.id(Integer.toString(person.getId()));
        }
        return mrsPerson;
    }

    private static String getAddress(Person person) {
        String address = null;
        final Set<PersonAddress> addresses = person.getAddresses();
        if (!addresses.isEmpty()) {
            address = addresses.iterator().next().getAddress1();
        }
        return address;
    }

    private static PersonName getFirstName(Set<PersonName> names) {
        List<PersonName> personNames = filter(having(on(PersonName.class).isPreferred(), is(false)), names);
        if (CollectionUtils.isEmpty(personNames)) {
            personNames = filter(having(on(PersonName.class).isPreferred(), is(true)), names);
        }
        return (!personNames.isEmpty()) ? personNames.get(0) : null;
    }

    public static Person createPerson(MRSPerson person, List<PersonAttributeType> allPersonAttributeTypes) {
        Person converted = new Person();
        if (person.getDateOfBirth() != null) {
            converted.setBirthdate(person.getDateOfBirth().toDate());
        }
        if (person.getDeathDate() != null) {
            converted.setDeathDate(person.getDeathDate().toDate());
        }
        converted.setBirthdateEstimated((Boolean) ObjectUtils.defaultIfNull(person.getBirthDateEstimated(), false));
        converted.setDead(person.isDead());
        converted.setGender(person.getGender());
        converted.addName(new PersonName(person.getFirstName(), person.getMiddleName(), person.getLastName()));

        if (person.getAddress() != null) {
            PersonAddress personAddress = new PersonAddress();
            personAddress.setAddress1(person.getAddress());
            converted.addAddress(personAddress);
        }

        for (MRSAttribute attribute : person.getAttributes()) {
            PersonAttributeType attributeType = (PersonAttributeType) selectUnique(allPersonAttributeTypes,
                    having(on(PersonAttributeType.class).getName(), equalTo(attribute.getName())));
            converted.addAttribute(new PersonAttribute(attributeType, attribute.getValue()));
        }
        return converted;
    }

    public static void updatePersonName(Person openMrsPerson, MRSPerson person) {
        if (StringUtils.isNotEmpty(person.getPreferredName())) {
            if (openMrsPerson.getNames().size() == 2) {
                for (PersonName name : openMrsPerson.getNames()) {
                    if (name.isPreferred()) {
                        name.setGivenName(person.getPreferredName());
                    } else {
                        name.setGivenName(person.getFirstName());
                    }
                    name.setMiddleName(person.getMiddleName());
                    name.setFamilyName(person.getLastName());
                }
            } else {
                PersonName personName = openMrsPerson.getPersonName();
                personName.setGivenName(person.getFirstName());
                personName.setMiddleName(person.getMiddleName());
                personName.setFamilyName(person.getLastName());
                PersonName preferredName = new PersonName(person.getPreferredName(), person.getMiddleName(), person.getLastName());
                preferredName.setPreferred(true);
                openMrsPerson.addName(preferredName);
            }
        } else {
            PersonName personName = openMrsPerson.getPersonName();
            personName.setGivenName(person.getFirstName());
            personName.setMiddleName(person.getMiddleName());
            personName.setFamilyName(person.getLastName());
        }
    }
}
