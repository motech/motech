package org.motechproject.appointments.api.model.search;

import ch.lambdaj.Lambda;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

public class UnvisitedCriterion implements Criterion {

    @Override
    public List<VisitResponse> filter(List<VisitResponse> visitResponses) {
        return Lambda.filter(having(on(VisitResponse.class).getVisitDate(), equalTo(null)), visitResponses);
    }

    @Override
    public List<VisitResponse> fetch(AllAppointmentCalendars allAppointmentCalendars)  {
        return allAppointmentCalendars.findMissedVisits();
    }
}
