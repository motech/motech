package org.motechproject.event.aggregation.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum AggregationState {
    Running,
    Paused;

    @JsonCreator
    public static AggregationState create(String value) {
        return AggregationState.valueOf(value.substring(0, 1).toUpperCase() + value.substring(1));
    }

    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }
}
