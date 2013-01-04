package org.motechproject.event.aggregation.model.schedule;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.event.aggregation.service.CronBasedAggregation;
import org.motechproject.event.aggregation.service.CronBasedAggregationRequest;

public class CronBasedAggregationRecord extends AggregationScheduleRecord implements CronBasedAggregation {
    private static final long serialVersionUID = 5729041837895097802L;
    @JsonProperty
    private String cronExpression;

    public CronBasedAggregationRecord() {
    }

    public CronBasedAggregationRecord(CronBasedAggregationRequest request) {
        this.cronExpression = request.getCronExpression();
    }

    public CronBasedAggregationRecord(String cronExpression) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CronBasedAggregationRecord)) {
            return false;
        }

        CronBasedAggregationRecord that = (CronBasedAggregationRecord) o;

        if (!cronExpression.equals(that.cronExpression)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return cronExpression.hashCode();
    }
}
