package org.motechproject.openmrs.helper;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.Facility;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class PatientHelperTest {

    private PatientHelper patientHelper;

    @Before
    public void setUp() {
        patientHelper = new PatientHelper();
    }

    @Test
    public void shouldGetFirstName() {
        Person person = new Person();
        PersonName firstName = new PersonName("firstname", "middlename", "familyname");
        PersonName preferredName = new PersonName("preferredname", "middlename", "familyname");
        preferredName.setPreferred(true);
        person.addName(firstName);
        person.addName(preferredName);
        assertThat(patientHelper.getFirstName(new Patient(person)), is(equalTo(firstName)));
    }

    @Test
    public void shouldGetPreferredName() {
        Person person = new Person();
        PersonName firstName = new PersonName("firstname", "middlename", "familyname");
        PersonName preferredName = new PersonName("preferredname", "middlename", "familyname");
        preferredName.setPreferred(true);
        person.addName(firstName);
        person.addName(preferredName);
        assertThat(patientHelper.getPreferredName(new Patient(person)), is(equalTo(preferredName.getGivenName())));
    }

    @Test
    public void shouldGetPatientAddress() {
        String expectedAddress = "Expected Patient Address";

        Patient patient = new Patient();
        PersonAddress address = new PersonAddress();
        address.setAddress1(expectedAddress);
        patient.addAddress(address);
        assertThat(patientHelper.getAddress(patient), is(equalTo(expectedAddress)));
    }

    @Test
    public void shouldBuildOpenMrsPatientModel() {
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String preferred = "preferred";
        final Date birthdate = new Date(1970, 3, 11);
        final String gender = "male";
        final String address = "a good street in ghana";
        final Facility facility = new Facility("1000", "name", "country", "region", "district", "province");
        final String attributeName1 = "name1";
        final String attributeValue1 = "value1";
        final List<Attribute> patientAttributes = Arrays.asList(new Attribute(attributeName1, attributeValue1));
        final String patientIdFromGenerator = "1";
        Boolean birthDateEstimated = true;
        org.motechproject.mrs.model.Patient patient1 = new org.motechproject.mrs.model.Patient(patientIdFromGenerator, first, middle, last, preferred, birthdate, birthDateEstimated, gender, address, patientAttributes, facility);
        final String motechId = "1000";
        final PatientIdentifierType patientIndentifierType = new PatientIdentifierType(2000);
        final Location location = new Location(3000);
        PersonAttributeType attributeType1 = new PersonAttributeType() {{ setName(attributeName1); setPersonAttributeTypeId(1000);}};
        PersonAttributeType attributeType2 = new PersonAttributeType() {{ setName("name2"); setPersonAttributeTypeId(1001);}};
        final List<PersonAttributeType> allPersonAttributeTypes = Arrays.asList(attributeType1, attributeType2);

        Patient returnedPatient = patientHelper.buildOpenMrsPatient(patient1, motechId, patientIndentifierType, location, allPersonAttributeTypes);

        assertThat(returnedPatient.getPatientIdentifier().getIdentifier(), is(equalTo(patientIdFromGenerator)));
        assertThat(returnedPatient.getPatientIdentifier().getIdentifierType(), is(equalTo(patientIndentifierType)));
        assertThat(returnedPatient.getPatientIdentifier().getLocation(), is(equalTo(location)));
        assertThat(returnedPatient.getGender(), is(equalTo(gender)));
        assertThat(returnedPatient.getBirthdate(), is(equalTo(birthdate)));
        assertThat(returnedPatient.getBirthdateEstimated(), is(equalTo(birthDateEstimated)));
        assertThat(returnedPatient.getPersonAddress().getAddress1(), is(equalTo(address)));
        assertThat(returnedPatient.getAttributes().size(), is(1));
        assertThat(returnedPatient.getAttribute(attributeName1).getValue(), is(equalTo(attributeValue1)));
        org.motechproject.mrs.model.Patient patient2 = new org.motechproject.mrs.model.Patient("", first, middle, last, preferred, birthdate, birthDateEstimated, gender, address, facility);
        returnedPatient = patientHelper.buildOpenMrsPatient(patient2, motechId, patientIndentifierType, location, allPersonAttributeTypes);

        assertThat(returnedPatient.getPatientIdentifier().getIdentifier(), is(equalTo(motechId)));

    }
}
