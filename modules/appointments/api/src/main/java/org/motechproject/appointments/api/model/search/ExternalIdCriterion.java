package org.motechproject.appointments.api.model.search;

import ch.lambdaj.Lambda;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;
import org.motechproject.appointments.api.service.contract.VisitResponse;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

public class ExternalIdCriterion implements Criterion {
    private String externalId;

    public ExternalIdCriterion(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public List<VisitResponse> filter(List<VisitResponse> visitResponses) {
        return Lambda.filter(having(on(VisitResponse.class).getExternalId(), equalTo(externalId)), visitResponses);
    }

    @Override
    public List<VisitResponse> fetch(AllAppointmentCalendars allAppointmentCalendars) {
        return allAppointmentCalendars.findVisitsByExternalId(externalId);
    }
}
