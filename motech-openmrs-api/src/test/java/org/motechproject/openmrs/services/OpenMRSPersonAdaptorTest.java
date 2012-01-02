package org.motechproject.openmrs.services;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSPerson;
import org.openmrs.*;
import org.openmrs.api.PersonService;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSPersonAdaptorTest {

    OpenMRSPersonAdaptor openMRSPersonAdaptor;

    public static final String PERSON_ATTRIBUTE_TYPE_EMAIL = "Email";
    public static final String PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER = "Phone Number";
    public static final String PERSON_ATTRIBUTE_TYPE_STAFF_TYPE = "Staff Type";

    @Mock
    private PersonService mockPersonService;


    @Before
    public void setUp() {
        initMocks(this);
        openMRSPersonAdaptor = new OpenMRSPersonAdaptor(mockPersonService);

    }

     @Test
    public void shouldGetFirstName() {
        final PersonName firstName = new PersonName("firstname", "middlename", "familyname");
        final PersonName preferredName = new PersonName("preferredname", "middlename", "familyname");
        preferredName.setPreferred(true);
        assertThat(openMRSPersonAdaptor.getFirstName(new HashSet<PersonName>(){{
            add(firstName);
            add(preferredName);
        }}), is(equalTo(firstName)));
    }

    @Test
    public void shouldGetPreferredName() {
        final PersonName firstName = new PersonName("firstname", "middlename", "familyname");
        final PersonName preferredName = new PersonName("preferredname", "middlename", "familyname");
        preferredName.setPreferred(true);
        MatcherAssert.assertThat(openMRSPersonAdaptor.getPreferredName(new HashSet<PersonName>(){{
            add(firstName);
            add(preferredName);
        }}), Matchers.is(Matchers.equalTo(preferredName.getGivenName())));
    }

    @Test
    public void shouldConvertOpenMRSPersonToMRSPersonWithoutPreferredName() {
        Person person = new Person();
        PersonAddress personAddress = new PersonAddress();
        person.setId(89);
        personAddress.setAddress1("Address1");
        person.setGender("F");
        person.setBirthdate(new Date());
        person.addName(new PersonName("FirstName", "MiddleName", "FamilyName"));
        person.setBirthdateEstimated(true);
        person.addAddress(personAddress);
        person.setDead(true);

        String email = "email";
        String phoneNo = "123423423";
        String staffType = "staffType";

        person.setAttributes(personAttributes(staffType, phoneNo, email));

        MRSPerson mrsPerson = openMRSPersonAdaptor.openMRSToMRSPerson(person);

        assertThat(mrsPerson.getFirstName(), is(equalTo(person.getGivenName())));
        assertThat(mrsPerson.getMiddleName(), is(equalTo(person.getMiddleName())));
        assertThat(mrsPerson.getLastName(), is(equalTo(person.getFamilyName())));
        assertThat(mrsPerson.getGender(), is(equalTo(person.getGender())));
        assertThat(mrsPerson.getAddress(), is(equalTo(person.getPersonAddress().getAddress1())));
        assertThat(mrsPerson.getBirthDateEstimated(),is(equalTo(person.getBirthdateEstimated())));
        assertThat(mrsPerson.getDateOfBirth(),is(equalTo(person.getBirthdate())));
        assertThat(mrsPerson.attrValue(PERSON_ATTRIBUTE_TYPE_EMAIL),is(equalTo(email)));
        assertThat(mrsPerson.attrValue(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER),is(equalTo(phoneNo)));
        assertThat(mrsPerson.attrValue(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE),is(equalTo(staffType)));
        assertThat(mrsPerson.isDead(), is(equalTo(true)));

    }

    @Test
    public void shouldGetPersonById(){
        String id = "10";
        Person person = mock(Person.class);
        when(mockPersonService.getPerson(Integer.valueOf(id))).thenReturn(person);
        Person openMRSPerson = openMRSPersonAdaptor.getPersonById(id);
        assertThat(openMRSPerson,is(equalTo(person)));
    }

    private Set<PersonAttribute> personAttributes(String staffType, String phoneNo, String email) {
        return new HashSet<PersonAttribute>(asList(new PersonAttribute(personAttributeType(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE), staffType),
                new PersonAttribute(personAttributeType(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER), phoneNo),
                new PersonAttribute(personAttributeType(PERSON_ATTRIBUTE_TYPE_EMAIL), email)));
    }

    private PersonAttributeType personAttributeType(String name) {
        PersonAttributeType attr = new PersonAttributeType((int) (Math.random() * 10000));
        attr.setName(name);
        return attr;
    }



}
