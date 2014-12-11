package org.motechproject.scheduler.trigger;

import org.joda.time.Period;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.spi.MutableTrigger;

/**
 * PeriodIntervalScheduleBuilder is responsible for creation of
 * {@link org.motechproject.scheduler.trigger.PeriodIntervalTriggerImpl}
 */
public class PeriodIntervalScheduleBuilder extends ScheduleBuilder<PeriodIntervalTrigger> {

    private Period repeatPeriod;

    private int misfireInstruction = PeriodIntervalTrigger.MISFIRE_INSTRUCTION_SMART_POLICY;

    protected PeriodIntervalScheduleBuilder() {
    }

    /**
     * Create a PeriodIntervalScheduleBuilder.
     *
     * @return the new PeriodIntervalScheduleBuilder
     */
    public static PeriodIntervalScheduleBuilder periodIntervalSchedule() {
        return new PeriodIntervalScheduleBuilder();
    }

    /**
     * Build the actual Trigger -- NOT intended to be invoked by end users,
     * but will rather be invoked by a TriggerBuilder which this
     * ScheduleBuilder is given to.
     */
    @Override
    public MutableTrigger build() {

        PeriodIntervalTriggerImpl pt = new PeriodIntervalTriggerImpl();
        pt.setRepeatPeriod(repeatPeriod);
        pt.setMisfireInstruction(misfireInstruction);

        return pt;
    }

    public PeriodIntervalScheduleBuilder withRepeatPeriod(Period repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
        return this;
    }

    public PeriodIntervalScheduleBuilder withMisfireHandlingInstructionIgnoreMisfires() {
        misfireInstruction = Trigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY;
        return this;
    }

    public PeriodIntervalScheduleBuilder withMisfireHandlingInstructionDoNothing() {
        misfireInstruction = PeriodIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING;
        return this;
    }

    public PeriodIntervalScheduleBuilder withMisfireHandlingInstructionFireAndProceed() {
        misfireInstruction = PeriodIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
        return this;
    }
}
