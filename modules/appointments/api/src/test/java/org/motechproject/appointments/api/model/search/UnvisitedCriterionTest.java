package org.motechproject.appointments.api.model.search;

import org.junit.Test;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;
import org.motechproject.appointments.api.service.contract.VisitResponse;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.util.DateUtil.newDateTime;

public class UnvisitedCriterionTest {

    
    @Test
    public void shouldFilterVisitsWhichAreDueInTimeRange() {
        VisitResponse visit1 = new VisitResponse();
        VisitResponse visit2 = new VisitResponse();
        visit2.setVisitDate(newDateTime(2012, 1, 1, 0, 0, 0));
        VisitResponse visit3 = new VisitResponse();
        visit3.setVisitDate(newDateTime(2012, 1, 2, 0, 0, 0));
        VisitResponse visit4 = new VisitResponse();
        List<VisitResponse> visits = asList(visit1, visit2, visit3, visit4);

        assertEquals(asList(new VisitResponse[]{visit1, visit4}), new UnvisitedCriterion().filter(visits));
    }

    @Test
    public void shouldFetchVisitsByCriteriaFromDb(){

        AllAppointmentCalendars allAppointmentCalendars = mock(AllAppointmentCalendars.class);
        List<VisitResponse> visitResponses = new ArrayList<VisitResponse>();

        when(allAppointmentCalendars.findMissedVisits()).thenReturn(visitResponses);
        assertThat(new UnvisitedCriterion().fetch(allAppointmentCalendars), is(visitResponses));
        verify(allAppointmentCalendars).findMissedVisits();
    }
}
