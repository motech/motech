package org.motechproject.event.aggregation.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.event.aggregation.service.AggregatedEvent;

import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.now;

@TypeDiscriminator("doc.type === 'AggregatedEvent'")
public class AggregatedEventRecord extends MotechBaseDataObject implements AggregatedEvent {

    @JsonProperty
    private String aggregationRuleName;

    @JsonProperty
    private Map<String, Object> aggregationParams;

    @JsonProperty
    private Map<String, Object> nonAggregationParams;

    @JsonProperty
    private DateTime timeStamp;

    @JsonProperty
    private boolean hasError;

    private AggregatedEventRecord() {
        super("AggregatedEvent");
    }

    public AggregatedEventRecord(String aggregationRuleName, Map<String, Object> aggregationParams, Map<String, Object> nonAggregationParams, boolean hasError) {
        this();
        this.aggregationRuleName = aggregationRuleName;
        this.aggregationParams = aggregationParams;
        this.nonAggregationParams = nonAggregationParams;
        this.hasError = hasError;
        this.timeStamp = now();
    }

    public AggregatedEventRecord(String aggregationRuleName, Map<String, Object> aggregationParams, Map<String, Object> nonAggregationParams) {
        this(aggregationRuleName, aggregationParams, nonAggregationParams, false);
    }

    @JsonIgnore
    public String getAggregationRuleName() {
        return aggregationRuleName;
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

    @JsonIgnore
    public boolean hasError() {
        return hasError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AggregatedEventRecord)) {
            return false;
        }

        AggregatedEventRecord that = (AggregatedEventRecord) o;

        if (!aggregationRuleName.equals(that.aggregationRuleName)) {
            return false;
        }
        if (!aggregationParams.equals(that.aggregationParams)) {
            return false;
        }
        if (!nonAggregationParams.equals(that.nonAggregationParams)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = aggregationRuleName.hashCode();
        result = 31 * result + aggregationParams.hashCode();
        result = 31 * result + nonAggregationParams.hashCode();
        return result;
    }
}
