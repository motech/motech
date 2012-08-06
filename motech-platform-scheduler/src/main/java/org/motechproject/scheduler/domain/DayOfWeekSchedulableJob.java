package org.motechproject.scheduler.domain;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

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
    private boolean intervening;

    public DayOfWeekSchedulableJob(MotechEvent motechEvent, LocalDate start, LocalDate end, List<DayOfWeek> days, Time time, boolean intervening) {
        this.intervening = intervening;
        if (motechEvent == null || start == null || end == null || days == null || isEmpty(days)) {
            throw new IllegalArgumentException("null/empty arguments");
        }
        this.motechEvent = motechEvent;
        this.start = start;
        this.end = end;
        this.time = time;
        this.days = days;
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

    public boolean isIntervening() {
        return intervening;
    }

    public List<Integer> getCronDays() {
        List<Integer> cronDays = new ArrayList<>();
        for (DayOfWeek day : days) {
            cronDays.add(new Integer(day.getCronValue()));
        }
        return cronDays;
    }
}

