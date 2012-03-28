package org.motechproject.appointments.api.model.search;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.util.DateUtil.inRange;

public class DueDateInCriterion implements Criterion {

    private DateTime start;
    private DateTime end;

    public DueDateInCriterion(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public List<VisitResponse> filter(List<VisitResponse> visitResponses) {
        List<VisitResponse> filteredVisits = new ArrayList<VisitResponse>();
        for (VisitResponse visitResponse : visitResponses) {
            DateTime dueDate = visitResponse.getAppointmentDueDate();
            if (inRange(dueDate, start, end))
                filteredVisits.add(visitResponse);
        }
        return filteredVisits;
    }

    @Override
    public List<VisitResponse> fetch(AllAppointmentCalendars allAppointmentCalendars) {
        return allAppointmentCalendars.findVisitsWithDueInRange(start,end);
    }
}
