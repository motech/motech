package org.motechproject.openmrs.rest.model;

import java.util.List;

public class LocationListResult {
    private List<Location> results;

    public List<Location> getResults() {
        return results;
    }

    public void setResults(List<Location> results) {
        this.results = results;
    }
}
