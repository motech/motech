package org.motechproject.event.aggregation.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.event.aggregation.service.AggregatedEventResult;

import java.util.ArrayList;
import java.util.List;

public class Aggregation {

    @JsonProperty
    private String aggregationRuleName;

    @JsonProperty("events")
    private List<AggregatedEventRecord> eventRecords;

    private Aggregation() {
    }

    public Aggregation(String aggregationRuleName, List<AggregatedEventRecord> eventRecords) {
        this.aggregationRuleName = aggregationRuleName;
        this.eventRecords = eventRecords;
    }

    @JsonIgnore
    public String getAggregationRuleName() {
        return aggregationRuleName;
    }

    @JsonIgnore
    public List<AggregatedEventRecord> getEventRecords() {
        return eventRecords;
    }

    @JsonIgnore
    public List<AggregatedEventResult> getEvents() {
        List<AggregatedEventResult> eventResults = new ArrayList<>();
        for (AggregatedEventRecord event : eventRecords) {
            eventResults.add(new AggregatedEventResult(event.getAggregationParams(), event.getNonAggregationParams(), event.getTimeStamp()));
        }
        return eventResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Aggregation)) {
            return false;
        }

        Aggregation that = (Aggregation) o;

        if (!aggregationRuleName.equals(that.aggregationRuleName)) {
            return false;
        }
        if (!eventRecords.equals(that.eventRecords)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = aggregationRuleName.hashCode();
        result = 31 * result + eventRecords.hashCode();
        return result;
    }
}
