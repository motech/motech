package org.motechproject.scheduler.trigger;

import org.joda.time.Period;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * Trigger that allows user to run Jobs every {@link org.joda.time.Period}
 * of time
 */
public interface PeriodIntervalTrigger extends Trigger {

    int MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1;

    int MISFIRE_INSTRUCTION_DO_NOTHING = 2;

    Period getRepeatPeriod();

    int getTimesTriggered();

    TriggerBuilder<PeriodIntervalTrigger> getTriggerBuilder();
}