package org.motechproject.appointments.api.model.search;

import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetadataPropertyCriterion implements Criterion {

    private final String property;
    private final Object value;

    public MetadataPropertyCriterion(String property, Object value) {
        this.property = property;
        this.value = value;
    }

    @Override
    public List<VisitResponse> filter(List<VisitResponse> visits) {
        List<VisitResponse> filteredVisits = new ArrayList<VisitResponse>();
        for (VisitResponse visit : visits) {
            if (visitHasMetadata(visit, property, value))
                filteredVisits.add(visit);
        }
        return filteredVisits;
    }

    private boolean visitHasMetadata(VisitResponse visit, String property, Object value) {
        Map<String, Object> visitData = visit.getVisitData();
        if (visitData.containsKey(property) && visitData.get(property).equals(value))
            return true;
        return false;
    }

    @Override
    public List<VisitResponse> fetch(AllAppointmentCalendars allAppointmentCalendars) {
        return allAppointmentCalendars.findByMetadataProperty(property, value);
    }
}
