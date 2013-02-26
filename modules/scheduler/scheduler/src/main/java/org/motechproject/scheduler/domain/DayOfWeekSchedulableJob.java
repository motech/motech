package org.motechproject.scheduler.domain;

import org.joda.time.LocalDate;
import org.motechproject.event.MotechEvent;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Job that is scheduled on particular days of week
 */
public final class DayOfWeekSchedulableJob implements Serializable {

    private static final long serialVersionUID = 1L;

    private final MotechEvent motechEvent;
    private final LocalDate start;
    private final LocalDate end;
    private final List<DayOfWeek> days;
    private Time time;
    private boolean ignorePastFiresAtStart;

    public DayOfWeekSchedulableJob(MotechEvent motechEvent, LocalDate start, LocalDate end, List<DayOfWeek> days, Time time, boolean ignorePastFiresAtStart) {
        if (motechEvent == null || hasNoDates(start, end) || isEmpty(days)) {
            throw new IllegalArgumentException("null/empty arguments");
        }
        this.motechEvent = motechEvent;
        this.start = start;
        this.end = end;
        this.time = time;
        this.days = days;
        this.ignorePastFiresAtStart = ignorePastFiresAtStart;
    }

    public DayOfWeekSchedulableJob(MotechEvent motechEvent, LocalDate start, LocalDate end, List<DayOfWeek> days, Time time) {
        this(motechEvent, start, end, days, time, false);
    }

    private boolean hasNoDates(LocalDate start, LocalDate end) {
        return start == null || end == null;
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

    public List<Integer> getCronDays() {
        List<Integer> cronDays = new ArrayList<>();
        for (DayOfWeek day : days) {
            cronDays.add(Integer.valueOf(day.getCronValue()));
        }
        return cronDays;
    }
}

