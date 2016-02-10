package org.motechproject.scheduler.contract;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired set number of times
 */
public class RepeatingSchedulableJob extends MisfireSchedulableJob {

    private static final long serialVersionUID = 1L;

    private Integer repeatCount;
    private Integer repeatIntervalInSeconds;

    /**
     * Constructor. It will create a job, which will never end, won't ignore past fires at start and will use original fire time after misfire.
     * Start time, {@code MotechEvent}, repeat count and repeat interval are not assigned, which means that further usage, without setting them, can cause exceptions.
     */
    public RepeatingSchedulableJob() {
        this(null, null, null, null, null, false, false, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} which will be fired when the job triggers, not null
     * @param repeatCount  the number of times job should be repeated, null treated as infinite
     * @param repeatIntervalInSeconds  the interval(in seconds) between job fires
     * @param startTime  the {@code DateTime} at which job should become ACTIVE, not null
     * @param endTime  the {@code DateTime} at which job should be stopped, null treated as never end
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not
     */
    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Integer repeatCount, final Integer repeatIntervalInSeconds,
                                   final DateTime startTime, final DateTime endTime, boolean ignorePastFiresAtStart) {
        this(motechEvent, repeatCount, repeatIntervalInSeconds, startTime, endTime, ignorePastFiresAtStart, false, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} which will be fired when the job triggers, not null
     * @param repeatIntervalInSeconds  the interval(in seconds) between job fires
     * @param startTime  the {@code DateTime} at which job should become ACTIVE, not null
     * @param endTime  the {@code DateTime} at which job should be stopped, null treated as never end
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not
     */
    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Integer repeatIntervalInSeconds,
                                   final DateTime startTime, final DateTime endTime, boolean ignorePastFiresAtStart) {
        this(motechEvent, null, repeatIntervalInSeconds, startTime, endTime, ignorePastFiresAtStart);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} which will be fired when the job triggers, not null
     * @param repeatCount  the number of times job should be repeated, null treated as infinite
     * @param repeatIntervalInSeconds  the interval(in seconds) between job fires
     * @param startTime  the {@code DateTime} at which job should become ACTIVE, not null
     * @param endTime  the {@code DateTime} at which job should be stopped, null treated as never end
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not
     */
    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Integer repeatCount,
                                   final Integer repeatIntervalInSeconds, final DateTime startTime,
                                   final DateTime endTime, boolean ignorePastFiresAtStart,
                                   boolean useOriginalFireTimeAfterMisfire, boolean uiDefined) {
        super(motechEvent, startTime, endTime, uiDefined, ignorePastFiresAtStart, useOriginalFireTimeAfterMisfire);
        this.repeatCount = repeatCount;
        this.repeatIntervalInSeconds = repeatIntervalInSeconds;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public RepeatingSchedulableJob setRepeatCount(final Integer repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }

    public Integer getRepeatIntervalInSeconds() {
        return repeatIntervalInSeconds;
    }

    public RepeatingSchedulableJob setRepeatIntervalInSeconds(final Integer repeatIntervalInSeconds) {
        this.repeatIntervalInSeconds = repeatIntervalInSeconds;
        return this;
    }

    @Override
    public String toString() {
        return "RepeatingSchedulableJob [motechEvent=" + getMotechEvent()
                + ", startTime=" + getStartDate() + ", endTime=" + getEndDate()
                + ", repeatCount=" + repeatCount + ", repeatIntervalInSeconds="
                + repeatIntervalInSeconds + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RepeatingSchedulableJob)) {
            return false;
        }
        RepeatingSchedulableJob job = (RepeatingSchedulableJob) obj;

        return ObjectUtils.equals(repeatCount, job.repeatCount)
                && ObjectUtils.equals(repeatIntervalInSeconds, job.repeatIntervalInSeconds)
                && super.equals(job);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ObjectUtils.hashCode(getMotechEvent());
        hash = hash * 31 + ObjectUtils.hashCode(getStartDate());
        hash = hash * 31 + ObjectUtils.hashCode(getEndDate());
        hash = hash * 31 + ObjectUtils.hashCode(repeatCount);
        hash = hash * 31 + ObjectUtils.hashCode(repeatIntervalInSeconds);
        hash = hash * 31 + ObjectUtils.hashCode(isIgnorePastFiresAtStart());
        hash = hash * 31 + ObjectUtils.hashCode(isUseOriginalFireTimeAfterMisfire());

        return hash;
    }
}
