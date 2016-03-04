package org.motechproject.scheduler.contract;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.event.MotechEvent;

/**
 * Job that will be fired every {@link org.joda.time.Period} of time
 */
public class RepeatingPeriodSchedulableJob extends MisfireSchedulableJob {

    private static final long serialVersionUID = 1L;

    private Period repeatPeriod;

    public RepeatingPeriodSchedulableJob() {
        this(null, null, null, null, false, false, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} which will be fired when the job triggers, not null
     * @param startTime  the {@code Date} at which job should become ACTIVE, not null
     * @param endTime  the {@code Date} at which job should be stopped, null treated as never end
     * @param repeatPeriod the {@code Period} between job fires, not null
     * @param ignorePastFiresAtStart the flag defining whether job should ignore past fires at start or not
     */
    public RepeatingPeriodSchedulableJob(final MotechEvent motechEvent, final DateTime startTime, final DateTime endTime,
                                         final Period repeatPeriod, boolean ignorePastFiresAtStart) {
        this(motechEvent, startTime, endTime, repeatPeriod, ignorePastFiresAtStart, false, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} which will be fired when the job triggers, not null
     * @param startTime  the {@code DateTime} at which job should become ACTIVE, not null
     * @param endTime  the {@code DateTime} at which job should be stopped, null treated as never end
     * @param repeatPeriod the {@code Period} between job fires, not null
     * @param ignorePastFiresAtStart the flag defining whether job should ignore past fires at start or not
     */
    public RepeatingPeriodSchedulableJob(final MotechEvent motechEvent, final DateTime startTime, final DateTime endTime,
                                         final Period repeatPeriod, boolean ignorePastFiresAtStart,
                                         boolean useOriginalFireTimeAfterMisfire, boolean uiDefined) {
        super(motechEvent, startTime, endTime, uiDefined, ignorePastFiresAtStart, useOriginalFireTimeAfterMisfire);
        this.repeatPeriod = repeatPeriod;
    }

    public Period getRepeatPeriod() {
        return repeatPeriod;
    }

    public void setRepeatPeriod(Period repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }

    @Override
    public String toString() {
        return "RepeatingSchedulableJob [motechEvent=" + getMotechEvent()
                + ", startTime=" + getStartDate() + ", endTime=" + getEndDate()
                + ", repeatPeriod=" + repeatPeriod + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RepeatingPeriodSchedulableJob)) {
            return false;
        }
        RepeatingPeriodSchedulableJob job = (RepeatingPeriodSchedulableJob) obj;

        return ObjectUtils.equals(repeatPeriod, job.repeatPeriod)
                && super.equals(job);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ObjectUtils.hashCode(getMotechEvent());
        hash = hash * 31 + ObjectUtils.hashCode(getStartDate());
        hash = hash * 31 + ObjectUtils.hashCode(getEndDate());
        hash = hash * 31 + ObjectUtils.hashCode(repeatPeriod);
        hash = hash * 31 + ObjectUtils.hashCode(isIgnorePastFiresAtStart());
        hash = hash * 31 + ObjectUtils.hashCode(isUseOriginalFireTimeAfterMisfire());

        return hash;
    }
}
