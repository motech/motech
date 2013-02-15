package org.motechproject.openmrs.ws.resource.impl;

import org.junit.Before;
import org.junit.Test;
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

public class ConceptResourceImplTest extends AbstractResourceImplTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldHandleConceptListResultJson() throws IOException, HttpException {
        String json = readJsonFromFile("json/concept-list-response.json");
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(json);

        ConceptResourceImpl impl = new ConceptResourceImpl(getClient(), getInstance());
        ConceptListResult result = impl.queryForConceptsByName("test");

        assertEquals(asList("Test Concept"), extract(result.getResults(), on(Concept.class).getDisplay()));
    }
}
