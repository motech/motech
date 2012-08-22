package org.motechproject.appointments.api.service.contract;

import java.util.ArrayList;
import java.util.List;

/**
 * \ingroup appointments
 *
 * Appointment schedule template for creating multiple appointments/scheduled visits.
 * It takes {@link CreateVisitRequest} list and user id and schedules appointments/visits.
 */
public class AppointmentCalendarRequest {

    private String externalId;
    private List<CreateVisitRequest> createVisitRequests = new ArrayList<CreateVisitRequest>();

    public String getExternalId() {
        return externalId;
    }

    public AppointmentCalendarRequest setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public List<CreateVisitRequest> getCreateVisitRequests() {
        return createVisitRequests;
    }

    public AppointmentCalendarRequest setCreateVisitRequests(List<CreateVisitRequest> createVisitRequests) {
        this.createVisitRequests = createVisitRequests;
        return this;
    }

    public AppointmentCalendarRequest addVisitRequest(CreateVisitRequest createVisitRequest) {
        this.createVisitRequests.add(createVisitRequest);
        return this;
    }
}
