package org.motechproject.appointments.api.model.search;

import ch.lambdaj.Lambda;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;
import org.motechproject.appointments.api.service.contract.VisitResponse;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MetadataCriterionTest {
    @Mock
    private AllAppointmentCalendars allAppointmentCalendars;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFetchVisitsMatchingGivenMetadataValueDirectlyFromTheDb() {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.addVisitData("foo", "bar");

        List<VisitResponse> result = new ArrayList<VisitResponse>();
        when(allAppointmentCalendars.findByMetadataProperty("foo", "bar")).thenReturn(result);
        MetadataCriterion criterion = new MetadataCriterion("foo", "bar");

        assertEquals(result, criterion.fetch(allAppointmentCalendars));
    }

    @Test
    public void shouldFilterVisitsMatchingGivenMetadataValue() {
        List<VisitResponse> visits = new ArrayList<VisitResponse>();
        visits.add(new VisitResponse().setName("visit1").addVisitData("foo", "bar"));
        visits.add(new VisitResponse().setName("visit2").addVisitData("goo", "bar"));
        visits.add(new VisitResponse().setName("visit3").addVisitData("Goo", "bar").addVisitData("foo", "bar"));
        visits.add(new VisitResponse().setName("visit4").addVisitData("foo", "baz"));

        MetadataCriterion criterion = new MetadataCriterion("foo", "bar");
        assertEquals(asList("visit1", "visit3"), Lambda.extract(criterion.filter(visits), on(VisitResponse.class).getName()));
    }
}
