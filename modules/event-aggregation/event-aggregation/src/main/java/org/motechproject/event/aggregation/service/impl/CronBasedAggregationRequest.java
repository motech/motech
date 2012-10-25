package org.motechproject.event.aggregation.service.impl;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.event.aggregation.service.CronBasedAggregation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CronBasedAggregationRequest extends AggregationScheduleRequest implements CronBasedAggregation {

    @NotNull(message = "Must specify cron expression")
    @Size(min = 1, message = "Must specify cron expression")
    @JsonProperty
    private String cronExpression;

    public CronBasedAggregationRequest() {
    }

    public CronBasedAggregationRequest(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @JsonIgnore
    @Override
    public String getCronExpression() {
        return cronExpression;
    }

    @JsonIgnore
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
