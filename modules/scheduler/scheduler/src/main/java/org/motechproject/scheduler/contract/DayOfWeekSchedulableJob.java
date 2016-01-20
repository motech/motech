package org.motechproject.scheduler.contract;

import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Job that is scheduled on particular days of week
 */
public final class DayOfWeekSchedulableJob extends SchedulableJob {

    private static final long serialVersionUID = 1L;

    private final MotechEvent motechEvent;
    private final LocalDate start;
    private final LocalDate end;
    private final List<DayOfWeek> days;
    private Time time;
    private boolean ignorePastFiresAtStart;

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job triggers, not null
     * @param start  the {@code Date} at which job should become ACTIVE, not null
     * @param end  the {@code Date} at which job should be stopped, null treated as never end
     * @param days  the list of days at which job should be fired, not null
     * @param time  the time at which job should be fired, not null
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not
     */
    public DayOfWeekSchedulableJob(MotechEvent motechEvent, LocalDate start, LocalDate end, List<DayOfWeek> days, Time time, boolean ignorePastFiresAtStart) {
        if (motechEvent == null || start == null || isEmpty(days)) {
            throw new IllegalArgumentException("null/empty arguments");
        }
        this.motechEvent = motechEvent;
        this.start = start;
        this.end = end;
        this.time = time;
        this.days = days;
        this.ignorePastFiresAtStart = ignorePastFiresAtStart;
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job triggers, not null
     * @param start  the {@code Date} at which job should become ACTIVE, not null
     * @param end  the {@code Date} at which job should be stopped, null treated as never end
     * @param days  the list of days at which job should be fired, not null
     * @param time  the time at which job should be fired, not null
     */
    public DayOfWeekSchedulableJob(MotechEvent motechEvent, LocalDate start, LocalDate end, List<DayOfWeek> days, Time time) {
        this(motechEvent, start, end, days, time, false);
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public LocalDate getStartDate() {
        return start;
    }

    public LocalDate getEndDate() {
        return end;
    }

    public Time getTime() {
        return time;
    }

    public boolean isIgnorePastFiresAtStart() {
        return ignorePastFiresAtStart;
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

