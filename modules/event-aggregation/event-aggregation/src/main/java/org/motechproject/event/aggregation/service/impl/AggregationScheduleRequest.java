package org.motechproject.event.aggregation.service.impl;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.event.aggregation.service.AggregationSchedule;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "scheduleType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PeriodicAggregationRequest.class, name = "periodic_request"),
    @JsonSubTypes.Type(value = CronBasedAggregationRequest.class, name = "cron_request"),
    @JsonSubTypes.Type(value = CustomAggregationRequest.class, name = "custom_request")
})
public abstract class AggregationScheduleRequest implements AggregationSchedule {

}
