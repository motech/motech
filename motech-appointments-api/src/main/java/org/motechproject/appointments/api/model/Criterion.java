package org.motechproject.appointments.api.model;

import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;

import java.util.List;

public interface Criterion {
    List<VisitResponse> filter(List<VisitResponse> visits);
    List<VisitResponse> fetch(AllAppointmentCalendars allAppointmentCalendars);
}
