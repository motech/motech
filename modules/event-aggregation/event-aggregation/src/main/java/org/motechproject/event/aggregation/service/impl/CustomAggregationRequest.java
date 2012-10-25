package org.motechproject.event.aggregation.service.impl;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.event.aggregation.model.validate.ValidRuleExpression;
import org.motechproject.event.aggregation.service.CustomAggregation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CustomAggregationRequest extends AggregationScheduleRequest implements CustomAggregation {

    @NotNull(message = "Must specify rule expression")
    @Size(min = 1, message = "Must specify rule expression")
    @ValidRuleExpression
    @JsonProperty
    private String rule;

    public CustomAggregationRequest() {
    }

    public CustomAggregationRequest(String rule) {
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
}
