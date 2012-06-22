package org.motechproject.openmrs.rest.model;

import java.util.List;

public class ObservationListResult {
    List<Observation> results;

    public List<Observation> getResults() {
        return results;
    }

    public void setResults(List<Observation> results) {
        this.results = results;
    }
}
