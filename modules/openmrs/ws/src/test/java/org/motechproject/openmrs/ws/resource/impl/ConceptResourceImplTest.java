package org.motechproject.openmrs.ws.resource.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.ConceptListResult;

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

        assertEquals(1, result.getResults().size());
    }
}
