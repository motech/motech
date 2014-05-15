package org.motechproject.event.aggregation.model.event;

import org.joda.time.DateTime;

import java.util.Map;

public interface AggregatedEvent {

    Map<String, Object> getAggregationParams();
    Map<String, Object> getNonAggregationParams();
    DateTime getTimeStamp();
}
