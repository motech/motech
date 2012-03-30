package org.motechproject.appointments.api.model.search;

import org.junit.Test;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;


public class ExternalIdCriterionTest  {

    @Test
    public void ShouldFilterVisitsByExternalId() {
         VisitResponse visitResponse1 = getVisitResponseInstance("foo1"),visitResponse2 = getVisitResponseInstance("foo2"),visitResponse3 = getVisitResponseInstance("foo2");
         assertEquals(asList(new VisitResponse[]{visitResponse2,visitResponse3}),new ExternalIdCriterion("foo2").filter(asList(new VisitResponse[]{visitResponse1,visitResponse2,visitResponse3})));
    }

    @Test
    public void shouldFetchVisitsByCriteriaFromDb(){
        AllAppointmentCalendars allAppointmentCalendars = mock(AllAppointmentCalendars.class);
        List<VisitResponse> visitResponses = new ArrayList<VisitResponse>();

        when(allAppointmentCalendars.findVisitsByExternalId("foo")).thenReturn(visitResponses);
        assertThat(new ExternalIdCriterion("foo").fetch(allAppointmentCalendars), is(visitResponses));
        verify(allAppointmentCalendars).findVisitsByExternalId("foo");
    }

    public VisitResponse getVisitResponseInstance(String externalId){
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setExternalId(externalId);
        return visitResponse;
    }
}
