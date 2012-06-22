package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.model.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationOpenMrsWS.xml" })
public class MRSPatientAdapterImplIT {

    private static final String MOTECH_ID_2 = "200-09";
    private static final String MOTECH_ID_3 = "300-10";
    private static final String TEMPORARY_ATTRIBUTE_VALUE = "Temporary Value";
    private static final String TEMPORARY_ATTRIBUTE_TYPE_NAME = "Temporary Attribute Type";
    private final MotechJsonReader reader = new MotechJsonReader();

    @Autowired
    AdapterHelper adapterHelper;

    @Autowired
    MRSPatientAdapter patientAdapter;

    @Autowired
    protected RestClient restfulClient;

    @Autowired
    protected RestOperations restOperations;

    @Value("${openmrs.url}")
    protected String openmrsUrl;

    @Test
    public void shouldCreatePatientWithAttributes() throws HttpException, URISyntaxException {
        String attributeUuid = null;
        MRSFacility facility = null;
        MRSPatient patient = null;
        try {
            facility = adapterHelper.createTemporaryLocation();
            attributeUuid = createTemporaryAttributeType(TEMPORARY_ATTRIBUTE_TYPE_NAME);

            MRSPerson person = TestUtils.makePerson();
            addAttributeToPatient(person, TEMPORARY_ATTRIBUTE_TYPE_NAME);

            patient = adapterHelper.createTemporaryPatient(TestUtils.MOTECH_ID_1, person, facility);

            MRSPatient persistedPatient = patientAdapter.getPatient(patient.getId());
            MRSPerson persistedPerson = persistedPatient.getPerson();

            assertNotNull(persistedPatient);
            assertEquals(TestUtils.MOTECH_ID_1, persistedPatient.getMotechId());

            assertEquals(TestUtils.TEST_PERSON_FIRST_NAME, persistedPerson.getFirstName());
            assertEquals(TestUtils.TEST_PERSON_MIDDLE_NAME, persistedPerson.getMiddleName());
            assertEquals(TestUtils.TEST_PERSON_LAST_NAME, persistedPerson.getLastName());
            assertEquals(TestUtils.TEST_PERSON_GENDER, persistedPerson.getGender());
            assertEquals(TestUtils.TEST_PERSON_ADDRESS, persistedPerson.getAddress());
            assertEquals(TestUtils.CURRENT_DATE, persistedPerson.getDateOfBirth());
            assertEquals(1, persistedPerson.getAttributes().size());
            assertEquals(TEMPORARY_ATTRIBUTE_TYPE_NAME, persistedPerson.getAttributes().get(0).name());
            assertEquals(TEMPORARY_ATTRIBUTE_VALUE, persistedPerson.getAttributes().get(0).value());
        } finally {
            adapterHelper.deletePatient(patient);
            deleteAttributeType(attributeUuid);
            adapterHelper.deleteFacility(facility);
        }
    }

    private void addAttributeToPatient(MRSPerson person, String attributeName) {
        person.addAttribute(new org.motechproject.mrs.model.Attribute(attributeName, TEMPORARY_ATTRIBUTE_VALUE));
    }

    private String createTemporaryAttributeType(String attributeName) throws HttpException, URISyntaxException {
        Attribute attr = new Attribute();
        attr.setName(attributeName);
        attr.setDescription("Temporary Attibute Type 2");
        attr.setFormat("java.lang.String");
        Gson gson = new GsonBuilder().create();

        String responseJson = restfulClient.postForJson(new URI(openmrsUrl + "/ws/rest/v1/personattributetype"),
                gson.toJson(attr));
        attr = (Attribute) reader.readFromString(responseJson, Attribute.class);
        return attr.getUuid();
    }

    private void deleteAttributeType(String attributeUuid) {
        restOperations.delete(openmrsUrl + "/ws/rest/v1/personattributetype/{uuid}" + "?purge", attributeUuid);
    }

    @Test
    public void shouldFindPatientByMotechId() throws HttpException, URISyntaxException {
        MRSFacility facility = null;
        MRSPatient patient = null;
        try {
            facility = adapterHelper.createTemporaryLocation();
            MRSPerson person = TestUtils.makePerson();
            patient = adapterHelper.createTemporaryPatient(TestUtils.MOTECH_ID_1, person, facility);

            MRSPatient persistedPatient = patientAdapter.getPatientByMotechId(TestUtils.MOTECH_ID_1);
            assertNotNull(persistedPatient);
        } finally {
            adapterHelper.deletePatient(patient);
            adapterHelper.deleteFacility(facility);
        }
    }

    @Test
    public void shouldSetPersonToDead() throws HttpException, URISyntaxException, PatientNotFoundException {
        MRSFacility facility = null;
        MRSPatient patient = null;
        try {
            facility = adapterHelper.createTemporaryLocation();
            MRSPerson person = TestUtils.makePerson();
            patient = adapterHelper.createTemporaryPatient(TestUtils.MOTECH_ID_1, person, facility);

            patientAdapter.deceasePatient(TestUtils.MOTECH_ID_1, "NONE", TestUtils.CURRENT_DATE, null);
            MRSPatient persistedPatient = patientAdapter.getPatient(patient.getId());

            assertTrue(persistedPatient.getPerson().isDead());
            assertEquals(TestUtils.CURRENT_DATE, persistedPatient.getPerson().deathDate());
        } finally {
            adapterHelper.deletePatient(patient);
            adapterHelper.deleteFacility(facility);
        }
    }

    @Test
    public void shouldFindPatientsOnSearch() throws HttpException, URISyntaxException {
        MRSFacility facility = null;
        MRSPatient patient1 = null;
        MRSPatient patient2 = null;
        MRSPatient patient3 = null;
        try {
            facility = adapterHelper.createTemporaryLocation();
            MRSPerson person1 = TestUtils.makePerson();
            patient1 = adapterHelper.createTemporaryPatient(TestUtils.MOTECH_ID_1, person1, facility);
            MRSPerson person2 = TestUtils.makePerson();
            patient2 = adapterHelper.createTemporaryPatient(MOTECH_ID_2, person2, facility);
            MRSPerson person3 = TestUtils.makePerson();
            patient3 = adapterHelper.createTemporaryPatient(MOTECH_ID_3, person3, facility);

            List<MRSPatient> patients = patientAdapter.search(TestUtils.TEST_PERSON_FIRST_NAME, null);

            assertEquals(3, patients.size());
            assertEquals(TestUtils.MOTECH_ID_1, patients.get(0).getMotechId());
            assertEquals(MOTECH_ID_2, patients.get(1).getMotechId());
            assertEquals(MOTECH_ID_3, patients.get(2).getMotechId());
        } finally {
            adapterHelper.deletePatient(patient1);
            adapterHelper.deletePatient(patient2);
            adapterHelper.deletePatient(patient3);
            adapterHelper.deleteFacility(facility);
        }
    }

    @Test
    public void shouldFindPatientsContainingMotechId() throws HttpException, URISyntaxException {
        MRSFacility facility = null;
        MRSPatient patient1 = null;
        MRSPatient patient2 = null;
        MRSPatient patient3 = null;
        try {
            facility = adapterHelper.createTemporaryLocation();
            MRSPerson person1 = TestUtils.makePerson();
            patient1 = adapterHelper.createTemporaryPatient(TestUtils.MOTECH_ID_1, person1, facility);
            MRSPerson person2 = TestUtils.makePerson();
            patient2 = adapterHelper.createTemporaryPatient(MOTECH_ID_2, person2, facility);
            MRSPerson person3 = TestUtils.makePerson();
            patient3 = adapterHelper.createTemporaryPatient(MOTECH_ID_3, person3, facility);

            List<MRSPatient> patients = patientAdapter.search(TestUtils.TEST_PERSON_FIRST_NAME, "200");

            assertEquals(2, patients.size());
            assertEquals(TestUtils.MOTECH_ID_1, patients.get(0).getMotechId());
            assertEquals(MOTECH_ID_2, patients.get(1).getMotechId());
        } finally {
            adapterHelper.deletePatient(patient1);
            adapterHelper.deletePatient(patient2);
            adapterHelper.deletePatient(patient3);
            adapterHelper.deleteFacility(facility);
        }
    }

    @Test
    public void shouldUpdatePatient() throws HttpException, URISyntaxException {
        String attributeUuid1 = null;
        String attributeUuid2 = null;
        MRSFacility facility = null;
        MRSPatient patient = null;
        try {
            facility = adapterHelper.createTemporaryLocation();
            attributeUuid1 = createTemporaryAttributeType(TEMPORARY_ATTRIBUTE_TYPE_NAME);
            attributeUuid2 = createTemporaryAttributeType(TEMPORARY_ATTRIBUTE_TYPE_NAME + 2);

            MRSPerson person = TestUtils.makePerson();
            addAttributeToPatient(person, TEMPORARY_ATTRIBUTE_TYPE_NAME);

            patient = adapterHelper.createTemporaryPatient(TestUtils.MOTECH_ID_1, person, facility);

            patient.getPerson().firstName("Changed First");
            patient.getPerson().lastName("Changed Last");
            patient.getPerson().getAttributes().remove(0);
            addAttributeToPatient(person, TEMPORARY_ATTRIBUTE_TYPE_NAME + 2);

            patientAdapter.updatePatient(patient);

            MRSPatient persistedPatient = patientAdapter.getPatient(patient.getId());
            MRSPerson persistedPerson = persistedPatient.getPerson();
            assertEquals("Changed First", persistedPerson.getFirstName());
            assertEquals("Changed Last", persistedPerson.getLastName());
            assertEquals(1, persistedPerson.getAttributes().size());
            assertEquals(TEMPORARY_ATTRIBUTE_TYPE_NAME + 2, persistedPerson.getAttributes().get(0).name());
        } finally {
            adapterHelper.deletePatient(patient);
            deleteAttributeType(attributeUuid1);
            deleteAttributeType(attributeUuid2);
            adapterHelper.deleteFacility(facility);
        }
    }
}
