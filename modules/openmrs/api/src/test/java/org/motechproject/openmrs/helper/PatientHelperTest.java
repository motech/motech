package org.motechproject.openmrs.helper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatientHelperTest {

    private PatientHelper patientHelper;

    @Before
    public void setUp() {
        patientHelper = new PatientHelper();
    }

    @Test
    public void shouldBuildOpenMrsPatientModel() {
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String preferred = "preferred";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final Date deathDate = new Date();
        final Boolean dead = true;
        final String gender = "male";
        final String address = "a good street in ghana";
        final OpenMRSFacility facility = new OpenMRSFacility("1000", "name", "country", "region", "district", "province");
        final String attributeName1 = "name1";
        final String attributeValue1 = "value1";
        final List<OpenMRSAttribute> patientAttributes = Arrays.asList(new OpenMRSAttribute(attributeName1, attributeValue1));
        final List<Attribute> attributes = new ArrayList<Attribute>();
        attributes.addAll(patientAttributes);
        final String patientIdFromGenerator = "1";
        Boolean birthDateEstimated = true;
        OpenMRSPerson mrsPerson = new OpenMRSPerson().firstName(first).middleName(middle).lastName(last).preferredName(preferred).birthDateEstimated(birthDateEstimated)
                .dateOfBirth(new DateTime(birthDate)).gender(gender).address(address).attributes(attributes).deathDate(new DateTime(deathDate)).dead(dead);
        OpenMRSPatient patient1 = new OpenMRSPatient(patientIdFromGenerator, mrsPerson,facility);
        final String motechId = "1000";
        final PatientIdentifierType patientIndentifierType = new PatientIdentifierType(2000);
        final Location location = new Location(3000);
        PersonAttributeType attributeType1 = new PersonAttributeType() {{ setName(attributeName1); setPersonAttributeTypeId(1000);}};
        PersonAttributeType attributeType2 = new PersonAttributeType() {{ setName("name2"); setPersonAttributeTypeId(1001);}};
        final List<PersonAttributeType> allPersonAttributeTypes = Arrays.asList(attributeType1, attributeType2);

        Patient returnedPatient = patientHelper.buildOpenMrsPatient(patient1, patientIndentifierType, location, allPersonAttributeTypes);

        assertThat(returnedPatient.getPatientIdentifier().getIdentifier(), is(equalTo(patientIdFromGenerator)));
        assertThat(returnedPatient.getPatientIdentifier().getIdentifierType(), is(equalTo(patientIndentifierType)));
        assertThat(returnedPatient.getPatientIdentifier().getLocation(), is(equalTo(location)));
        assertThat(returnedPatient.getGender(), is(equalTo(gender)));
        assertThat(returnedPatient.getBirthdate(), is(equalTo(birthDate)));
        assertThat(returnedPatient.getBirthdateEstimated(), is(equalTo(birthDateEstimated)));
        assertThat(returnedPatient.getPersonAddress().getAddress1(), is(equalTo(address)));
        assertThat(returnedPatient.getAttributes().size(), is(1));
        assertThat(returnedPatient.getAttribute(attributeName1).getValue(), is(equalTo(attributeValue1)));
        assertThat(returnedPatient.getDeathDate(), is(equalTo(deathDate)));
        assertThat(returnedPatient.isDead(), is(equalTo(dead)));
        String motechId1 = "";
        OpenMRSPatient patient2 = new OpenMRSPatient(motechId1, mrsPerson,facility);

        returnedPatient = patientHelper.buildOpenMrsPatient(patient2, patientIndentifierType, location, allPersonAttributeTypes);

        assertThat(returnedPatient.getPatientIdentifier().getIdentifier(), is(equalTo(motechId1)));

    }
}
