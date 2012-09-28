package org.motechproject.openmrs.ws.resource.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.ObservationListResult;

public class ObservationResourceImplTest extends AbstractResourceImplTest {

    private ObservationResourceImpl impl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        impl = new ObservationResourceImpl(getClient(), getInstance());
    }

    @Test
    public void shouldGetObservationByPatientId() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/observation-list-response.json"));

        ObservationListResult result = impl.queryForObservationsByPatientId("OOO");

        assertEquals(1, result.getResults().size());
    }

}
