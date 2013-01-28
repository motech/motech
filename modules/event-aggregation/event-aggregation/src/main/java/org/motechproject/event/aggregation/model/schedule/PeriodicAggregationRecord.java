package org.motechproject.event.aggregation.model.schedule;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.event.aggregation.service.PeriodicAggregation;

public class PeriodicAggregationRecord extends AggregationScheduleRecord implements PeriodicAggregation {
    private static final long serialVersionUID = 3403337605865847986L;
    @JsonProperty
    private Period period;
    @JsonProperty
    private DateTime startDate;

    public PeriodicAggregationRecord() {
    }

    public PeriodicAggregationRecord(Period period, DateTime startDate) {
        this.period = period;
        this.startDate = startDate;
    }

    @JsonIgnore
    @Override
    public Period getPeriod() {
        return period;
    }

    @JsonIgnore
    @Override
    public DateTime getStartTime() {
        return startDate;
    }

    @JsonIgnore
    public void setPeriod(Period period) {
        this.period = period;
    }

    @JsonIgnore
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PeriodicAggregationRecord)) {
            return false;
        }

        PeriodicAggregationRecord that = (PeriodicAggregationRecord) o;

        if (!period.equals(that.period)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return period.hashCode();
    }
}
