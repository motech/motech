package org.motechproject.openmrs.ws.resource.model;

import org.motechproject.openmrs.ws.resource.model.Attribute.AttributeType;

import java.util.List;

public class AttributeTypeListResult {
    private List<AttributeType> results;

    public List<AttributeType> getResults() {
        return results;
    }

    public void setResults(List<AttributeType> results) {
        this.results = results;
    }
}
