package org.motechproject.openmrs.ws.resource.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Observation;
import org.motechproject.openmrs.ws.resource.model.ObservationListResult;

import java.io.IOException;
import java.net.URI;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

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

        assertEquals(asList("OOO"), extract(result.getResults(), on(Observation.class).getUuid()));
    }

}
