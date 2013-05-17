package org.motechproject.openmrs.ws.resource.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Concept;
import org.motechproject.openmrs.ws.resource.model.ConceptListResult;

import java.io.IOException;
import java.net.URI;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConceptResourceImplTest extends AbstractResourceImplTest {

    private ConceptResourceImpl impl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        impl = new ConceptResourceImpl(getClient(), getInstance());
    }

    @Test
    public void shouldHandleConceptListResultJson() throws IOException, HttpException {
        String json = readJsonFromFile("json/concept-list-response.json");
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(json);

        ConceptResourceImpl impl = new ConceptResourceImpl(getClient(), getInstance());
        ConceptListResult result = impl.queryForConceptsByName("test");

        assertEquals(asList("Test Concept"), extract(result.getResults(), on(Concept.class).getDisplay()));
    }

    @Test
    public void shouldCreateConcept() throws IOException, HttpException {
        Concept concept = buildConcept();
        impl.createConcept(concept);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postForJson(Mockito.any(URI.class), captor.capture());

        String expectedJson = readJsonFromFile("json/concept-create.json");

        JsonElement expectedObj = getGson().fromJson(expectedJson, JsonObject.class);
        JsonElement sentObject = getGson().fromJson(captor.getValue(), JsonObject.class);

        assertEquals(expectedObj, sentObject);
    }

    private Concept buildConcept() {
        Concept concept = new Concept();
        concept.setName(new Concept.ConceptName());
        concept.getName().setName("Test Concept");
        concept.setDisplay("Test Concept");

        return concept;
    }

    @Test
    public void shouldParseAllConcepts() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/concept-list-response.json"));

        ConceptListResult result = impl.getAllConcepts();

        assertEquals(asList("8bb05db3-6c7b-474a-9f79-6f5dd3ad5002"), extract(result.getResults(), on(Concept.class).getUuid()));
    }

    @Test
    public void shouldQueryForConceptByName() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/concept-list-response.json"));

        ConceptListResult result = impl.queryForConceptsByName("Test");

        assertEquals(asList("8bb05db3-6c7b-474a-9f79-6f5dd3ad5002"), extract(result.getResults(), on(Concept.class).getUuid()));
    }

    @Test
    public void shouldUpdateConcept() throws IOException, HttpException {
        Concept concept = buildConcept();
        impl.updateConcept(concept);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postWithEmptyResponseBody(Mockito.any(URI.class), captor.capture());

        String expectedJson = readJsonFromFile("json/concept-create.json");

        Concept expectedObj = getGson().fromJson(expectedJson, Concept.class);
        Concept sentObject = getGson().fromJson(captor.getValue(), Concept.class);

        assertEquals(expectedObj.getUuid(), sentObject.getUuid());
        assertEquals(expectedObj.getDisplay(), sentObject.getDisplay());
    }

    @Test
    public void shouldParseSingleConcept() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/concept-create.json"));

        Concept concept = impl.getConceptById("LLL");

        assertNotNull(concept);
    }
}
