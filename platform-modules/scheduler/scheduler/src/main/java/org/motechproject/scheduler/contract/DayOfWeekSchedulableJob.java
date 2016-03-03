package org.motechproject.scheduler.contract;

import org.joda.time.DateTime;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Job that is scheduled on particular days of week
 */
public final class DayOfWeekSchedulableJob extends EndingSchedulableJob {

    private static final long serialVersionUID = 1L;

    private List<DayOfWeek> days;
    private Time time;

    public DayOfWeekSchedulableJob() {
        this(null, null, null, null, null, false, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job triggers, not null
     * @param startDate  the {@code Date} at which job should become ACTIVE, not null
     * @param endDate  the {@code Date} at which job should be stopped, null treated as never end
     * @param days  the list of days at which job should be fired, not null
     * @param time  the time at which job should be fired, not null
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not
     */
    public DayOfWeekSchedulableJob(MotechEvent motechEvent, DateTime startDate, DateTime endDate, List<DayOfWeek> days,
                                   Time time, boolean ignorePastFiresAtStart) {
        this(motechEvent, startDate, endDate, days, time, ignorePastFiresAtStart, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job triggers, not null
     * @param startDate  the {@code Date} at which job should become ACTIVE, not null
     * @param endDate  the {@code Date} at which job should be stopped, null treated as never end
     * @param days  the list of days at which job should be fired, not null
     * @param time  the time at which job should be fired, not null
     */
    public DayOfWeekSchedulableJob(MotechEvent motechEvent, DateTime startDate, DateTime endDate, List<DayOfWeek> days,
                                   Time time) {
        this(motechEvent, startDate, endDate, days, time, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job triggers, not null
     * @param startDate  the {@code Date} at which job should become ACTIVE, not null
     * @param endDate  the {@code Date} at which job should be stopped, null treated as never end
     * @param days  the list of days at which job should be fired, not null
     * @param time  the time at which job should be fired, not null
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not
     * @param uiDefined  the flag defining, whether job has been created through the UI
     */
    public DayOfWeekSchedulableJob(MotechEvent motechEvent, DateTime startDate, DateTime endDate,
                                   List<DayOfWeek> days, Time time, boolean ignorePastFiresAtStart, boolean uiDefined) {
        super(motechEvent, startDate, endDate, uiDefined, ignorePastFiresAtStart);
        this.days = days;
        this.time = time;
    }

    public Time getTime() {
        return time;
    }

    public List<DayOfWeek> getDays() {
        return days;
    }

    public void setDays(List<DayOfWeek> days) {
        this.days = days;
    }

    /**
     * Returns list of days(as {@code Integer}) at which Job should be fired.
     *
     * @return list of days(as {@code Integer}) at which Job should be fired
     */
    public List<Integer> getCronDays() {
        List<Integer> cronDays = new ArrayList<>();
        for (DayOfWeek day : days) {
            cronDays.add(Integer.valueOf(day.getCronValue()));
        }
        return cronDays;
    }
}

