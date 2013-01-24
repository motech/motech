package org.motechproject.event.aggregation.model.schedule;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.event.aggregation.service.CustomAggregation;
import org.motechproject.event.aggregation.service.impl.CustomAggregationRequest;

public class CustomAggregationRecord extends AggregationScheduleRecord implements CustomAggregation {
    private static final long serialVersionUID = 5795967601901810210L;
    @JsonProperty
    private String rule;

    public CustomAggregationRecord() {
    }

    public CustomAggregationRecord(CustomAggregationRequest request) {
        this.rule = request.getRule();
    }

    public CustomAggregationRecord(String rule) {
        this.rule = rule;
    }

    @JsonIgnore
    @Override
    public String getRule() {
        return rule;
    }

    @JsonIgnore
    public void setRule(String rule) {
        this.rule = rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomAggregationRecord)) {
            return false;
        }

        CustomAggregationRecord that = (CustomAggregationRecord) o;

        if (!rule.equals(that.rule)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return rule.hashCode();
    }
}
