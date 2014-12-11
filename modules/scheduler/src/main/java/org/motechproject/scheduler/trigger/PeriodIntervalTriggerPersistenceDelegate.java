package org.motechproject.scheduler.trigger;

import org.joda.time.Period;
import org.quartz.ScheduleBuilder;
import org.quartz.impl.jdbcjobstore.SimplePropertiesTriggerPersistenceDelegateSupport;
import org.quartz.impl.jdbcjobstore.SimplePropertiesTriggerProperties;
import org.quartz.spi.OperableTrigger;

/**
 * Responsible for handling communication between
 * {@link org.motechproject.scheduler.trigger.PeriodIntervalTrigger}
 * and database.
 */
public class PeriodIntervalTriggerPersistenceDelegate extends SimplePropertiesTriggerPersistenceDelegateSupport {

    @Override
    protected SimplePropertiesTriggerProperties getTriggerProperties(OperableTrigger trigger) {
        PeriodIntervalTriggerImpl periodTrig = (PeriodIntervalTriggerImpl) trigger;

        SimplePropertiesTriggerProperties props = new SimplePropertiesTriggerProperties();

        props.setString1(periodTrig.getRepeatPeriod().toString());
        props.setInt1(periodTrig.getTimesTriggered());
        return props;
    }

    @Override
    protected TriggerPropertyBundle getTriggerPropertyBundle(SimplePropertiesTriggerProperties properties) {
        Period repeatPeriod = Period.parse(properties.getString1());

        ScheduleBuilder<?> sb = PeriodIntervalScheduleBuilder.periodIntervalSchedule()
                .withRepeatPeriod(repeatPeriod);

        int timesTriggered = properties.getInt1();

        String[] statePropertyNames = {"timesTriggered"};
        Object[] statePropertyValues = {timesTriggered};

        return new TriggerPropertyBundle(sb, statePropertyNames, statePropertyValues);
    }

    @Override
    public boolean canHandleTriggerType(OperableTrigger trigger) {
        return ((trigger instanceof PeriodIntervalTriggerImpl) && !((PeriodIntervalTriggerImpl) trigger).hasAdditionalProperties());
    }

    @Override
    public String getHandledTriggerTypeDiscriminator() {
        return "PERIOD";
    }

}
