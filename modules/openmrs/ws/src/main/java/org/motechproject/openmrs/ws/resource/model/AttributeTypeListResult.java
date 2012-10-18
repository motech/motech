package org.motechproject.openmrs.ws.resource.model;

import java.util.List;

import org.motechproject.openmrs.ws.resource.model.Attribute.AttributeType;

public class AttributeTypeListResult {
    private List<AttributeType> results;

    public List<AttributeType> getResults() {
        return results;
    }

    public void setResults(List<AttributeType> results) {
        this.results = results;
    }
}
