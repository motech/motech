package org.motechproject.openmrs.rest.model;

import java.util.List;

public class ConceptListResult {

    private List<Concept> results;

    public List<Concept> getResults() {
        return results;
    }

    public void setResults(List<Concept> results) {
        this.results = results;
    }
}
