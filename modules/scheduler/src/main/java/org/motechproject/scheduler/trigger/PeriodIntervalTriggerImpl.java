package org.motechproject.scheduler.trigger;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.CoreTrigger;

import java.util.Date;

/**
 * Implementation of {@link org.motechproject.scheduler.trigger.PeriodIntervalTrigger}
 */
public class PeriodIntervalTriggerImpl extends AbstractTrigger<PeriodIntervalTrigger> implements PeriodIntervalTrigger, CoreTrigger {

    private static final long serialVersionUID = 3181854860621972894L;

    private static final int YEAR_TO_GIVEUP_SCHEDULING_AT = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + 100;

    private Date startTime;

    private Date endTime;

    private Date nextFireTime;

    private Date previousFireTime;

    private Period repeatPeriod;

    private int timesTriggered;

    private boolean complete;

    @Override
    public Date getStartTime() {
        if (startTime == null) {
            startTime = new Date();
        }
        return startTime;
    }

    @Override
    public void setStartTime(Date startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        Date eTime = getEndTime();
        if (eTime != null && eTime.before(startTime)) {
            throw new IllegalArgumentException(
                    "End time cannot be before start time");
        }

        this.startTime = startTime;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(Date endTime) {
        Date sTime = getStartTime();
        if (sTime != null && endTime != null && sTime.after(endTime)) {
            throw new IllegalArgumentException(
                    "End time cannot be before start time");
        }

        this.endTime = endTime;
    }

    public void setTimesTriggered(int timesTriggered) {
        this.timesTriggered = timesTriggered;
    }

    public int getTimesTriggered() {
        return timesTriggered;
    }

    public Period getRepeatPeriod() {
        return repeatPeriod;
    }

    public void setRepeatPeriod(Period repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }

    @Override
    protected boolean validateMisfireInstruction(int misfireInstruction) {
        return (misfireInstruction < MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) ? false : true;
    }

    @Override
    public void updateAfterMisfire(org.quartz.Calendar cal) {
        int instr = getMisfireInstruction();

        if (instr == MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
            return;
        }

        if (instr == MISFIRE_INSTRUCTION_SMART_POLICY) {
            instr = MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
        }

        if (instr == MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
            Date newFireTime = getFireTimeAfter(new Date());
            while (newFireTime != null && cal != null
                    && !cal.isTimeIncluded(newFireTime.getTime())) {
                newFireTime = getFireTimeAfter(newFireTime);
            }
            setNextFireTime(newFireTime);
        } else if (instr == MISFIRE_INSTRUCTION_DO_NOTHING) {
            setNextFireTime(new Date());
        }
    }

    /**
     * Called when the <code>{@link Scheduler}</code> has decided to 'fire'
     * the trigger (execute the associated <code>Job</code>), in order to
     * give the <code>Trigger</code> a chance to update itself for its next
     * triggering (if any).
     */
    @Override
    public void triggered(org.quartz.Calendar calendar) {
        timesTriggered++;
        previousFireTime = nextFireTime;
        nextFireTime = getFireTimeAfter(nextFireTime);

        while (nextFireTime != null && calendar != null
                && !calendar.isTimeIncluded(nextFireTime.getTime())) {

            nextFireTime = getFireTimeAfter(nextFireTime);

            if (nextFireTime == null) {
                break;
            }

            //avoid infinite loop
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(nextFireTime);
            if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
                nextFireTime = null;
            }
        }

        if (nextFireTime == null) {
            complete = true;
        }
    }

    @Override
    public void updateWithNewCalendar(org.quartz.Calendar calendar, long misfireThreshold)
    {
        nextFireTime = getFireTimeAfter(previousFireTime);

        if (nextFireTime == null || calendar == null) {
            return;
        }

        Date now = new Date();
        while (nextFireTime != null && !calendar.isTimeIncluded(nextFireTime.getTime())) {

            nextFireTime = getFireTimeAfter(nextFireTime);

            if (nextFireTime == null) {
                break;
            }

            //avoid infinite loop
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(nextFireTime);
            if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
                nextFireTime = null;
            }

            if (nextFireTime != null && nextFireTime.before(now)) {
                long diff = now.getTime() - nextFireTime.getTime();
                if (diff >= misfireThreshold) {
                    nextFireTime = getFireTimeAfter(nextFireTime);
                }
            }
        }
    }

    /**
     * Called by the scheduler at the time a <code>Trigger</code> is first
     * added to the scheduler, in order to have the <code>Trigger</code>
     * compute its first fire time, based on any associated calendar.
     *
     * After this method has been called, <code>getNextFireTime()</code>
     * should return a valid answer.
     *
     * @return the first time at which the <code>Trigger</code> will be fired
     *         by the scheduler, which is also the same value <code>getNextFireTime()</code>
     *         will return (until after the first firing of the <code>Trigger</code>).
     */
    @Override
    public Date computeFirstFireTime(org.quartz.Calendar calendar) {
        nextFireTime = getStartTime();

        while (nextFireTime != null && calendar != null
                && !calendar.isTimeIncluded(nextFireTime.getTime())) {

            nextFireTime = getFireTimeAfter(nextFireTime);

            if (nextFireTime == null) {
                break;
            }

            //avoid infinite loop
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(nextFireTime);
            if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
                return null;
            }
        }
        return  nextFireTime;
    }

    /**
     * Returns the next time at which the <code>Trigger</code> is scheduled to fire. If
     * the trigger will not fire again, <code>null</code> will be returned.  Note that
     * the time returned can possibly be in the past, if the time that was computed
     * for the trigger to next fire has already arrived, but the scheduler has not yet
     * been able to fire the trigger (which would likely be due to lack of resources
     * e.g. threads).
     *
     * The value returned is not guaranteed to be valid until after the <code>Trigger</code>
     * has been added to the scheduler.
     */
    @Override
    public Date getNextFireTime() {
        return nextFireTime;
    }

    @Override
    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    @Override
    public Date getFireTimeAfter(Date afterTime) {
        if (complete) {
            return null;
        }

        DateTime fireTime;
        if (afterTime == null) {
            fireTime = new DateTime(new Date(System.currentTimeMillis())).plus(repeatPeriod);
        } else {
            fireTime = new DateTime(new Date(afterTime.getTime())).plus(repeatPeriod);
        }

        if (endTime != null && endTime.before(fireTime.toDate())) {
            return null;
        }

        return fireTime.toDate();
    }

    @Override
    public Date getFinalFireTime() {
        if (complete || getEndTime() == null) {
            return null;
        }

        // back up a second from end time
        Date fTime = new Date(getEndTime().getTime() - 1000L);
        // find the next fire time after that
        fTime = getFireTimeAfter(fTime);

        // the the trigger fires at the end time, that's it!
        if (fTime.equals(getEndTime())) {
            return fTime;
        }

        DateTime finalTime = new DateTime(fTime).minus(repeatPeriod);
        return finalTime.toDate();
    }

    @Override
    public boolean mayFireAgain() {
        return (getNextFireTime() != null);
    }

    /**
     * Validates whether the properties of the <code>JobDetail</code> are
     * valid for submission into a <code>Scheduler</code>.
     *
     * @throws IllegalStateException
     *           if a required property (such as Name, Group, Class) is not
     *           set.
     */
    @Override
    public void validate() throws SchedulerException {
        super.validate();

        if (Period.ZERO.equals(repeatPeriod)) {
            throw new SchedulerException("Repeat Period must be at least 1 milisecond long.");
        }
    }

    /**
     * Get a {@link ScheduleBuilder} that is configured to produce a
     * schedule identical to this trigger's schedule.
     *
     * @see #getTriggerBuilder()
     */
    @Override
    public ScheduleBuilder<PeriodIntervalTrigger> getScheduleBuilder() {

        PeriodIntervalScheduleBuilder sb = PeriodIntervalScheduleBuilder.periodIntervalSchedule()
                .withRepeatPeriod(repeatPeriod)
                .withMisfireHandlingInstructionDoNothing();


        switch (getMisfireInstruction()) {
            case MISFIRE_INSTRUCTION_DO_NOTHING :
                sb.withMisfireHandlingInstructionDoNothing();
                break;
            case MISFIRE_INSTRUCTION_FIRE_ONCE_NOW :
                sb.withMisfireHandlingInstructionFireAndProceed();
                break;
        }

        return sb;
    }

    public boolean hasAdditionalProperties() {
        return false;
    }
}
