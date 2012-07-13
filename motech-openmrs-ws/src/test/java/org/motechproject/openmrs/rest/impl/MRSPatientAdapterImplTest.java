package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.util.DateUtil;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;

import com.google.gson.JsonElement;

public class MRSPatientAdapterImplTest {

    @Mock
    private RestClient client;

    @Mock
    private MRSFacilityAdapter facilityAdapter;

    @Mock
    private OpenMrsUrlHolder urlHolder;

    @Mock
    private MRSPersonAdapterImpl personAdapter;

    private MRSPatientAdapterImpl impl;

    private MotechJsonReader reader = new MotechJsonReader();

    @Before
    public void setUp() {
        initMocks(this);
        impl = new MRSPatientAdapterImpl(client, urlHolder, personAdapter, facilityAdapter, "MoTeCH Id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullPatient() {
        impl.savePatient(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullPatientMotechId() {
        impl.savePatient(new MRSPatient(null, null, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyPatientMotechId() {
        impl.savePatient(new MRSPatient("", null, null));
    }

    @Test(expected = MRSException.class)
    public void shouldThrowExceptionWhenMotechIdentifierTypeHttpFailure() throws HttpException, URISyntaxException {
        MRSPerson savedperson = new MRSPerson();
        savedperson.id("AAA");
        MRSFacility facility = new MRSFacility("");

        when(personAdapter.savePerson(null)).thenReturn(savedperson);
        when(client.getJson(null)).thenThrow(new HttpException(""));

        impl.savePatient(new MRSPatient("558", null, facility));
    }

    @Test(expected = MRSException.class)
    public void shouldThrowExceptionWhenMotechIdentifierIsNotFound() throws HttpException, URISyntaxException {
        MRSPerson savedperson = new MRSPerson();
        savedperson.id("");
        MRSFacility facility = new MRSFacility("");
        savedperson.id("AAA");
        String emptyResults = "{\"results\":[]}";

        when(personAdapter.savePerson(null)).thenReturn(savedperson);
        when(client.getJson(null)).thenReturn(emptyResults);

        impl.savePatient(new MRSPatient("558", null, facility));
    }

    @Test
    public void shouldConstructCorrectJsonForPatient() throws IOException, HttpException, URISyntaxException {
        JsonElement expected = TestUtils.parseJsonFile("json/patient-create.json");

        MRSPerson savedperson = new MRSPerson();
        savedperson.id("AAA");

        MRSFacility facility = new MRSFacility("LLL");

        String motechTypeUuidResult = TestUtils.parseJsonFileAsString("json/patient-identifier-list-response.json");

        when(personAdapter.savePerson(null)).thenReturn(savedperson);
        when(client.getJson(null)).thenReturn(motechTypeUuidResult);
        when(client.postForJson(any(URI.class), any(String.class))).thenReturn("{}");

        impl.savePatient(new MRSPatient("558", null, facility));

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(client).postForJson(any(URI.class), argument.capture());

        JsonElement result = (JsonElement) TestUtils.parseJsonString(argument.getValue());
        assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullMotechId() {
        impl.getPatientByMotechId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyMotechId() {
        impl.getPatientByMotechId("");
    }

    @Test(expected = MRSException.class)
    public void shouldThrowExceptionOnMotechIdHttpError() throws HttpException {
        when(client.getJson(null)).thenThrow(new HttpException(""));

        impl.getPatientByMotechId("111");
    }

    @Test
    public void shouldReturnNullOnEmptyResults() throws HttpException {
        when(client.getJson(null)).thenReturn("{\"results\":[]}");

        MRSPatient result = impl.getPatientByMotechId("111");

        assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullPatientId() {
        impl.getPatient(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyPatientId() {
        impl.getPatient("");
    }

    @Test(expected = MRSException.class)
    public void shouldThrowExceptionOnPatientIdHttpError() throws HttpException {
        when(client.getJson(null)).thenThrow(new HttpException(""));

        impl.getPatient("PPP");
    }

    @Test
    public void shouldParseJsonIntoMrsPatientObj() throws IOException, HttpException,
            ParseException {
        String patientJson = TestUtils.parseJsonFileAsString("json/patient-response.json");
        String motechIdentifierJsonResponse = TestUtils
                .parseJsonFileAsString("json/patient-identifier-list-response.json");

        MRSPatient expectedPatient = makeExpectedPatient();

        when(client.getJson(null)).thenReturn(patientJson).thenReturn(motechIdentifierJsonResponse);

        MRSPatient testPatient = impl.getPatient("PPP");

        assertEquals(expectedPatient, testPatient);
    }

    private MRSPatient makeExpectedPatient() throws ParseException {
        MRSPerson person = new MRSPerson();
        person.id("PPP").address("Addr 1").gender("M").preferredName("John Doe").birthDateEstimated(false).dead(false)
                .firstName("John").middleName("E").lastName("Doe").birthDateEstimated(true)
                .dateOfBirth(DateUtil.parseOpenMrsDate("1950-01-01T00:00:00.000-0500"));
        Attribute attr = new Attribute("Civil Status", "5555");
        person.addAttribute(attr);
        MRSPatient patient = new MRSPatient("PPP", "588", person, null);
        return patient;
    }

    @Test
    public void shouldDeceasePatient() throws IOException, ParseException, HttpException, PatientNotFoundException {
        String patientSearchResult = TestUtils.parseJsonFileAsString("json/patient-list-response.json");
        String patientJson = TestUtils.parseJsonFileAsString("json/patient-response.json");
        String motechIdentifierJsonResponse = TestUtils
                .parseJsonFileAsString("json/patient-identifier-list-response.json");

        when(client.getJson(null)).thenReturn(patientSearchResult).thenReturn(patientJson)
                .thenReturn(motechIdentifierJsonResponse);

        Date deathDate = new Date();
        impl.deceasePatient("588", "Test Concept", deathDate, null);

        verify(personAdapter).savePersonCauseOfDeath("PPP", deathDate, "Test Concept");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionNullPatientUpdate() {
        impl.updatePatient(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullPatientIdUpdate() {
        impl.updatePatient(new MRSPatient(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyPatientIdUpdate() {
        impl.updatePatient(new MRSPatient(""));
    }

    @Test
    public void shouldReturnOneResultFromSearch() throws IOException, HttpException {
        String patientSearchResult = TestUtils.parseJsonFileAsString("json/patient-list-multiple-response.json");
        String patientJson = TestUtils.parseJsonFileAsString("json/patient-response.json");
        String motechIdentifierJsonResponse = TestUtils
                .parseJsonFileAsString("json/patient-identifier-list-response.json");
        String patient2Json = TestUtils.parseJsonFileAsString("json/patient-response2.json");

        when(client.getJson(null)).thenReturn(patientSearchResult).thenReturn(patientJson)
                .thenReturn(motechIdentifierJsonResponse).thenReturn(patient2Json);

        List<MRSPatient> patients = impl.search("Doe", "588");

        assertEquals(1, patients.size());
    }

    @Test
    public void shouldReturnAllSearchedOrderedByMotechId() throws IOException, HttpException {
        String patientSearchResult = TestUtils.parseJsonFileAsString("json/patient-list-multiple-response.json");
        String patient2Json = TestUtils.parseJsonFileAsString("json/patient-response2.json");
        String motechIdentifierJsonResponse = TestUtils
                .parseJsonFileAsString("json/patient-identifier-list-response.json");
        String patientJson = TestUtils.parseJsonFileAsString("json/patient-response.json");

        when(client.getJson(null)).thenReturn(patientSearchResult).thenReturn(patientJson)
                .thenReturn(motechIdentifierJsonResponse).thenReturn(patient2Json);

        List<MRSPatient> patients = impl.search("Doe", null);

        assertEquals(2, patients.size());
        assertEquals(patients.get(0).getMotechId(), "588");
        assertEquals(patients.get(1).getMotechId(), "589");
    }
}
