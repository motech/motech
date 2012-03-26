package org.motechproject.appointments.api.model;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.motechproject.util.DateUtil.newDateTime;

public class DueDateInCriterionTest {

    @Test
    public void shouldFilterVisitsByDueInTimeRange() {
        VisitResponse visit1 = getVisitResponseInstance(newDateTime(2011, 3, 5, 1, 2, 3));
        VisitResponse visit2 = getVisitResponseInstance(newDateTime(2011, 5, 2, 23, 59, 59));
        VisitResponse visit3 = getVisitResponseInstance(newDateTime(2011, 5, 3, 0, 0, 0));
        VisitResponse visit4 = getVisitResponseInstance(newDateTime(2011, 7, 5, 1, 2, 3));
        VisitResponse visit5 = getVisitResponseInstance(newDateTime(2011, 8, 3, 0, 0, 0));
        VisitResponse visit6 = getVisitResponseInstance(newDateTime(2011, 8, 3, 0, 0, 3));
        List<VisitResponse> visits = asList(new VisitResponse[]{visit1, visit2, visit3, visit4, visit5, visit6});

        DateTime start = newDateTime(2011, 5, 3, 0, 0, 0);
        DateTime end = newDateTime(2011, 8, 3, 0, 0, 0);
        DueDateInCriterion dueDateInCriterion = new DueDateInCriterion(start, end);
        assertEquals(asList(new VisitResponse[]{visit3, visit4, visit5}), dueDateInCriterion.filter(visits));
    }
    
    @Test
    public void shouldFetchVisitsByCriteriaFromDb(){
        DateTime start = newDateTime(2011, 5, 3, 0, 0, 0);
        DateTime end = newDateTime(2011, 8, 3, 0, 0, 0);
        AllAppointmentCalendars allAppointmentCalendars = mock(AllAppointmentCalendars.class);
        List<VisitResponse> visitResponses = new ArrayList<VisitResponse>();

        when(allAppointmentCalendars.findVisitsWithDueInRange(start,end)).thenReturn(visitResponses);
        assertThat(new DueDateInCriterion(start, end).fetch(allAppointmentCalendars), is(visitResponses));
        verify(allAppointmentCalendars).findVisitsWithDueInRange(start, end);
    }
    
    public VisitResponse getVisitResponseInstance(DateTime appointmentDate){
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setAppointmentDueDate(appointmentDate);
        return visitResponse;
    }
    
    

}
