package org.motechproject.event.aggregation.service;

import org.joda.time.DateTime;
import org.joda.time.Period;

public interface PeriodicAggregation extends AggregationSchedule {

    Period getPeriod();
    DateTime getStartTime();
}
