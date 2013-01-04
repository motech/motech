package org.motechproject.event.aggregation.service;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.now;


public class AggregatedEventResult implements AggregatedEvent, Serializable {

    private static final long serialVersionUID = -1536678519770572131L;

    @JsonProperty
    private Map<String, Object> aggregationParams;

    @JsonProperty
    private Map<String, Object> nonAggregationParams;

    @JsonProperty
    private DateTime timeStamp;

    private AggregatedEventResult() {
    }

    public AggregatedEventResult(Map<String, Object> aggregationParams, Map<String, Object> nonAggregationParams) {
        this();
        this.aggregationParams = aggregationParams;
        this.nonAggregationParams = nonAggregationParams;
        this.timeStamp = now();
    }

    @JsonIgnore
    @Override
    public Map<String, Object> getAggregationParams() {
        return aggregationParams;
    }

    @JsonIgnore
    @Override
    public Map<String, Object> getNonAggregationParams() {
        return nonAggregationParams;
    }

    @JsonIgnore
    @Override
    public DateTime getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AggregatedEventResult)) {
            return false;
        }

        AggregatedEventResult that = (AggregatedEventResult) o;

        if (!aggregationParams.equals(that.aggregationParams)) {
            return false;
        }
        if (!nonAggregationParams.equals(that.nonAggregationParams)) {
            return false;
        }
        if (!timeStamp.equals(that.timeStamp)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = aggregationParams.hashCode();
        result = 31 * result + nonAggregationParams.hashCode();
        result = 31 * result + timeStamp.hashCode();
        return result;
    }
}
