package org.motechproject.openmrs.rest.model;

import java.util.List;

public class PatientIdentifierListResult {

    private List<IdentifierType> results;

    public List<IdentifierType> getResults() {
        return results;
    }

    public void setResults(List<IdentifierType> results) {
        this.results = results;
    }
}
