package org.motechproject.openmrs.helper;

import org.junit.Test;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.openmrs.Person;
import org.openmrs.PersonName;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PersonHelperTest {

    @Test
    public void shouldConvertPatientToOpenMrsPatient() {
        final int personID = 123;
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final Date birthDate = new Date();
        final Boolean dead = false;
        final String gender = "male";

        Set<PersonName> nameList = new HashSet<>();
        final PersonName firstName = new PersonName(first, middle, last);
        nameList.add(firstName);

        Person person = new Person();
        person.setPersonId(personID);
        person.setNames(nameList);
        person.setBirthdate(birthDate);
        person.setGender(gender);
        person.setDead(dead);

        OpenMRSPerson returnedPerson = PersonHelper.openMRSToMRSPerson(person);

        assertThat(returnedPerson.getGender(), is(equalTo(gender)));
        assertThat(returnedPerson.getFirstName(), is(equalTo(first)));
        assertThat(returnedPerson.getLastName(), is(equalTo(last)));
        assertThat(returnedPerson.getAttributes().size(), is(0));
        assertThat(returnedPerson.isDead(), is(equalTo(dead)));
    }

    @Test
    public void shouldUpdatePatientName() {
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";

        OpenMRSPerson openMRSPerson = new OpenMRSPerson();
        openMRSPerson.setFirstName(first);
        openMRSPerson.setMiddleName(middle);
        openMRSPerson.setLastName(last);

        Set<PersonName> nameList = new HashSet<>();
        final PersonName firstName = new PersonName("1", "2", "3");
        nameList.add(firstName);

        Person person = new Person();
        person.setNames(nameList);

        PersonHelper.updatePersonName(person, openMRSPerson);

        assertThat(person.getGivenName(), is(equalTo(first)));
        assertThat(person.getMiddleName(), is(equalTo(middle)));
        assertThat(person.getFamilyName(), is(equalTo(last)));
    }

}
