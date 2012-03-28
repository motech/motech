package org.motechproject.appointments.api.model.search;

import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;

import java.util.List;

public interface Criterion {
    List<VisitResponse> filter(List<VisitResponse> visits);
    List<VisitResponse> fetch(AllAppointmentCalendars allAppointmentCalendars);
}
