package org.motechproject.scheduler.contract;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

import java.io.Serializable;

import static org.codehaus.jackson.annotate.JsonSubTypes.Type;
import static org.motechproject.scheduler.constants.SchedulerConstants.CRON;
import static org.motechproject.scheduler.constants.SchedulerConstants.DAY_OF_WEEK;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEATING;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEATING_PERIOD;
import static org.motechproject.scheduler.constants.SchedulerConstants.RUN_ONCE;

/**
 * Represents Job that can be scheduled
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@jobType")
@JsonSubTypes({
        @Type(name = CRON, value = CronSchedulableJob.class),
        @Type(name = DAY_OF_WEEK, value = DayOfWeekSchedulableJob.class),
        @Type(name = REPEATING, value = RepeatingSchedulableJob.class),
        @Type(name = REPEATING_PERIOD, value = RepeatingPeriodSchedulableJob.class),
        @Type(name = RUN_ONCE, value = RunOnceSchedulableJob.class)
})
public abstract class SchedulableJob implements Serializable {

    private MotechEvent motechEvent;

    private DateTime startDate;

    private boolean uiDefined;

    private boolean ignorePastFiresAtStart;

    protected SchedulableJob() {
        this(null, null, false, false);
    }

    protected SchedulableJob(MotechEvent motechEvent, DateTime startDate, boolean uiDefined,
                             boolean ignorePastFiresAtStart) {
        this.motechEvent = motechEvent;
        this.startDate = startDate;
        this.uiDefined = uiDefined;
        this.ignorePastFiresAtStart = ignorePastFiresAtStart;
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public void setMotechEvent(MotechEvent motechEvent) {
        this.motechEvent = motechEvent;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public boolean isUiDefined() {
        return uiDefined;
    }

    public void setUiDefined(boolean uiDefined) {
        this.uiDefined = uiDefined;
    }

    public boolean isIgnorePastFiresAtStart() {
        return ignorePastFiresAtStart;
    }

    /**
     * Ignore past fires when start time of job is in past.
     * <pre>ex : repeating job with interval of 5 unit, and current time in between fire 2 and 3 will start triggering from 3rd firetime.
     *  1     2     3     4
     *  +-----+-----+-----+
     *  start    ^current time
     *  </pre>
     * @param ignorePastFiresAtStart
     */
    public void setIgnorePastFiresAtStart(boolean ignorePastFiresAtStart) {
        this.ignorePastFiresAtStart = ignorePastFiresAtStart;
    }

    protected boolean equals(SchedulableJob other) {
        return ObjectUtils.equals(motechEvent, other.motechEvent)
                && ObjectUtils.equals(startDate, other.startDate)
                && uiDefined == other.uiDefined
                && ignorePastFiresAtStart == other.ignorePastFiresAtStart;
    }
}
