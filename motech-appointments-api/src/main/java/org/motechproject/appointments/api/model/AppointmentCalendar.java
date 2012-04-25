package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type === 'AppointmentCalendar'")
public class AppointmentCalendar extends MotechBaseDataObject {
    private String externalId;
    @JsonProperty
    private List<Visit> visits = new ArrayList<Visit>();

    public String getExternalId() {
        return externalId;
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

    public AppointmentCalendar externalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public AppointmentCalendar addVisit(Visit newVisit) {
        for (Visit visit : visits) {
            if (visit.isSame(newVisit)) return this;
        }
        visits.add(newVisit);
        return this;
    }

    // For ektorp

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
