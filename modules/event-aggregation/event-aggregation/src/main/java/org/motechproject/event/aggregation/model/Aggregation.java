package org.motechproject.event.aggregation.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.event.aggregation.service.impl.AggregatedEventResult;

import java.util.List;

public class Aggregation {

    @JsonProperty
    private String aggregationRuleName;

    @JsonProperty
    private List<AggregatedEventResult> events;

    private Aggregation() {
    }

    public Aggregation(String aggregationRuleName, List<AggregatedEventResult> events) {
        this.aggregationRuleName = aggregationRuleName;
        this.events = events;
    }

    @JsonIgnore
    public String getAggregationRuleName() {
        return aggregationRuleName;
    }

    @JsonIgnore
    public List<AggregatedEventResult> getEvents() {
        return events;
    }
}
