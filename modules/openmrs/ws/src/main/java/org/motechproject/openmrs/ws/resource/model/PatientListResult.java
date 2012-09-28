package org.motechproject.openmrs.ws.resource.model;

import java.util.List;

public class PatientListResult {
    private List<Patient> results;

    public List<Patient> getResults() {
        return results;
    }

    public void setResults(List<Patient> results) {
        this.results = results;
    }
}
