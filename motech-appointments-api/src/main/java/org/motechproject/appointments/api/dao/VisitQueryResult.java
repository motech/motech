package org.motechproject.appointments.api.dao;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.appointments.api.model.Visit;

class VisitQueryResult {
    @JsonProperty
    private Visit visit;

    @JsonProperty
    private String externalId;

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
