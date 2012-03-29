package org.motechproject.appointments.api.model.search;

import ch.lambdaj.Lambda;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MetadataPropertyCriterionTest {

    @Mock
    private AllAppointmentCalendars allAppointmentCalendars;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFetchVisitsMatchingGivenMetadataValueDirectlyFromTheDb() {
        List<VisitResponse> result = mock(List.class);
        when(allAppointmentCalendars.findByMetadataProperty("foo", "bar")).thenReturn(result);
        MetadataPropertyCriterion criterion = new MetadataPropertyCriterion("foo", "bar");
        assertEquals(result, criterion.fetch(allAppointmentCalendars));
    }

    @Test
    public void shouldFilterVisitsMatchingGivenMetadataValue() {
        List<VisitResponse> visits = new ArrayList<VisitResponse>();
        visits.add(new VisitResponse().setName("visit1").addVisitData("foo", "bar"));
        visits.add(new VisitResponse().setName("visit2").addVisitData("goo", "bar"));
        visits.add(new VisitResponse().setName("visit3").addVisitData("Goo", "bar").addVisitData("foo", "bar"));
        visits.add(new VisitResponse().setName("visit4").addVisitData("foo", "baz"));

        MetadataPropertyCriterion criterion = new MetadataPropertyCriterion("foo", "bar");
        assertEquals(asList(new String[]{ "visit1", "visit3" }), Lambda.extract(criterion.filter(visits), on(VisitResponse.class).getName()));
    }
}
