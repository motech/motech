package org.motechproject.event.aggregation.service.impl;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.commons.date.util.JodaFormatter;
import org.motechproject.event.aggregation.model.validate.ValidPeriod;
import org.motechproject.event.aggregation.service.PeriodicAggregation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PeriodicAggregationRequest extends AggregationScheduleRequest implements PeriodicAggregation {

    @NotNull(message = "Must specify repeat period")
    @Size(min = 1, message = "Must specify repeat period")
    @ValidPeriod
    @JsonProperty
    private String period;
    @NotNull(message = "Must specify start time for the schedule")
    @JsonProperty
    private Long startTimeInMillis;

    public PeriodicAggregationRequest() {
    }

    public PeriodicAggregationRequest(String period, DateTime startTime) {
        this.period = period;
        if (startTime != null) {
            this.startTimeInMillis = startTime.getMillis();
        }
    }

    @JsonIgnore
    @Override
    public Period getPeriod() {
        return new JodaFormatter().parsePeriod(period);
    }

    @JsonIgnore
    @Override
    public DateTime getStartTime() {
        return new DateTime(startTimeInMillis);
    }

    @JsonIgnore
    public void setPeriod(Period period) {
        this.period = new JodaFormatter().formatPeriod(period);
    }

    @JsonIgnore
    public void setStartTimeInMillis(DateTime date) {
        this.startTimeInMillis = date.getMillis();
    }
}
