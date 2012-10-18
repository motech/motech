package org.motechproject.openmrs.ws.resource.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Identifier;
import org.motechproject.openmrs.ws.resource.model.IdentifierType;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.Patient;
import org.motechproject.openmrs.ws.resource.model.PatientListResult;
import org.motechproject.openmrs.ws.resource.model.Person;

import com.google.gson.JsonElement;

public class PatientResourceImplTest extends AbstractResourceImplTest {

    private PatientResourceImpl impl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        impl = new PatientResourceImpl(getClient(), getInstance());
    }

    @Test
    public void shouldCreatePatient() throws HttpException, IOException {
        Patient patient = buildPatient();

        impl.createPatient(patient);

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);

        Mockito.verify(getClient()).postForJson(Mockito.any(URI.class), sentJson.capture());

        String expectedJson = readJsonFromFile("json/patient-create.json");
        JsonElement expectedJsonObj = stringToJsonElement(expectedJson);
        JsonElement sentJsonObj = stringToJsonElement(sentJson.getValue());

        assertEquals(expectedJsonObj, sentJsonObj);
    }

    private Patient buildPatient() {
        Patient patient = new Patient();
        Person person = new Person();
        person.setUuid("AAA");
        patient.setPerson(person);

        Identifier identifier = new Identifier();
        Location location = new Location();
        location.setUuid("LLL");
        IdentifierType it = new IdentifierType();
        it.setUuid("III");

        identifier.setIdentifier("558");
        identifier.setLocation(location);
        identifier.setIdentifierType(it);

        return patient;
    }

    @Test
    public void shouldQueryForPatient() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/patient-list-response.json"));

        PatientListResult result = impl.queryForPatient("558");

        assertEquals(1, result.getResults().size());
    }

    @Test
    public void shouldGetPatientById() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/patient-response.json"));

        Patient patient = impl.getPatientById("123");

        assertNotNull(patient);
    }

    @Test
    public void shouldFindMotechIdentifierType() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/patient-identifier-list-response.json"));

        Mockito.when(getInstance().getMotechPatientIdentifierTypeName()).thenReturn("MoTeCH Id");
        String uuid = impl.getMotechPatientIdentifierUuid();

        assertEquals("III", uuid);
    }
}
