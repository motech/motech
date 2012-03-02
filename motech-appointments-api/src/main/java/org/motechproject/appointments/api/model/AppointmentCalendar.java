package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type === 'AppointmentCalendar'")
public class AppointmentCalendar extends MotechBaseDataObject {

    @JsonProperty
    private String externalId;

    @JsonProperty
    private List<Visit> visits = new ArrayList<Visit>();

    public String externalId() {
        return externalId;
    }

    public AppointmentCalendar externalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public List<Visit> visits() {
        return visits;
    }

    public Visit getVisit(String visitName) {
        for (Visit visit : visits) {
            if (visit.name().equalsIgnoreCase(visitName)) {
                return visit;
            }
        }
        return null;
    }

    public AppointmentCalendar addVisit(Visit visit) {
        visits.add(visit);
        return this;
    }
}
