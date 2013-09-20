package org.motechproject.event.aggregation.model.schedule;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.event.aggregation.model.schedule.AggregationSchedule;

public interface PeriodicAggregation extends AggregationSchedule {

    Period getPeriod();
    DateTime getStartTime();
}
