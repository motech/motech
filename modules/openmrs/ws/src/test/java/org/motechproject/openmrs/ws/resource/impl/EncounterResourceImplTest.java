package org.motechproject.openmrs.ws.resource.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Concept;
import org.motechproject.openmrs.ws.resource.model.Encounter;
import org.motechproject.openmrs.ws.resource.model.Encounter.EncounterType;
import org.motechproject.openmrs.ws.resource.model.EncounterListResult;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.Observation;
import org.motechproject.openmrs.ws.resource.model.Observation.ObservationValue;
import org.motechproject.openmrs.ws.resource.model.Patient;
import org.motechproject.openmrs.ws.resource.model.Person;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EncounterResourceImplTest extends AbstractResourceImplTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldWriteEncounterCreateJson() throws IOException, HttpException {
        Encounter encounter = getExpectedEncounter();

        EncounterResourceImpl impl = new EncounterResourceImpl(getClient(), getInstance());
        impl.createEncounter(encounter);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postForJson(Mockito.any(URI.class), captor.capture());

        String expectedJson = readJsonFromFile("json/encounter-create.json");
        JsonElement expectedJsonObj = getGson().fromJson(expectedJson, JsonObject.class);
        JsonElement foundJsonObj = getGson().fromJson(captor.getValue(), JsonObject.class);

        assertEquals(expectedJsonObj, foundJsonObj);
    }

    private Encounter getExpectedEncounter() {
        Encounter encounter = new Encounter();
        EncounterType type = new EncounterType();
        type.setName("ADULTINITIAL");
        encounter.setEncounterType(type);

        Location loc = new Location();
        loc.setUuid("LLL");
        encounter.setLocation(loc);

        Patient patient = new Patient();
        patient.setUuid("PPP");
        encounter.setPatient(patient);

        Person provider = new Person();
        provider.setUuid("PPR");
        encounter.setProvider(provider);

        Observation obs = new Observation();
        Concept concept = new Concept();
        concept.setDisplay("CCC");
        obs.setConcept(concept);
        ObservationValue value = new ObservationValue();
        value.setDisplay("Test Value");
        obs.setValue(value);
        List<Observation> observations = new ArrayList<Observation>();
        observations.add(obs);
        encounter.setObs(observations);

        return encounter;
    }

    @Test
    public void shouldReadEncounterListResult() throws IOException, HttpException {
        String responseJson = readJsonFromFile("json/encounter-by-patient-response.json");

        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(responseJson);

        EncounterResourceImpl impl = new EncounterResourceImpl(getClient(), getInstance());
        EncounterListResult result = impl.queryForAllEncountersByPatientId("200");

        assertEquals(1, result.getResults().size());
    }
}
