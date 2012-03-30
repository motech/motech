package org.motechproject.appointments.api.model.search;

import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;

import java.util.ArrayList;
import java.util.List;

public class MetadataCriterion implements Criterion {
    private final String property;
    private final String value;

    public MetadataCriterion(String property, String value) {
        this.property = property;
        this.value = value;
    }

    @Override
    public List<VisitResponse> fetch(AllAppointmentCalendars allAppointmentCalendars) {
        return allAppointmentCalendars.findByMetadataProperty(property, value);
    }

    @Override
    public List<VisitResponse> filter(List<VisitResponse> visits) {
        List<VisitResponse> filteredVisits = new ArrayList<VisitResponse>();
        for (VisitResponse visit : visits) {
            if (visit.hasMetadata(property, value))
                filteredVisits.add(visit);
        }
        return filteredVisits;
    }

}
