package org.motechproject.appointments.api.contract;

import java.util.HashMap;
import java.util.Map;

public class AppointmentCalendarRequest {

    private String externalId;
    private Map<String, VisitRequest> visitRequests = new HashMap<String, VisitRequest>();

    public String getExternalId() {
        return externalId;
    }

    public AppointmentCalendarRequest setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public Map<String, VisitRequest> getVisitRequests() {
        return visitRequests;
    }

    public AppointmentCalendarRequest setVisitRequests(Map<String, VisitRequest> visitRequests) {
        this.visitRequests = visitRequests;
        return this;
    }

    public AppointmentCalendarRequest addVisitRequest(String name, VisitRequest visitRequest) {
        this.visitRequests.put(name, visitRequest);
        return this;
    }
}
