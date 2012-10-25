package org.motechproject.event.aggregation.model.schedule;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.event.aggregation.service.AggregationSchedule;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "scheduleType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PeriodicAggregationRecord.class, name = "periodic"),
    @JsonSubTypes.Type(value = CronBasedAggregationRecord.class, name = "cron"),
    @JsonSubTypes.Type(value = CustomAggregationRecord.class, name = "custom")
})
public abstract class AggregationScheduleRecord implements AggregationSchedule {

    protected AggregationScheduleRecord() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AggregationScheduleRecord)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.hashCode();
    }
}
