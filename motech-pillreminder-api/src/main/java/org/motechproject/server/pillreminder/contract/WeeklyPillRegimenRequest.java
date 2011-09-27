package org.motechproject.server.pillreminder.contract;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfTheWeek;
import org.motechproject.model.Time;

public class WeeklyPillRegimenRequest {

    private String externalId;

    private Time bestCallTime;

    private DayOfTheWeek reminderDayOfTheWeek;

    private int retries;

    private int repeatIntervalInMinutes;

    private LocalDate startDate;

    public String getExternalId() {
        return externalId;
    }

    public Time getBestCallTime() {
        return bestCallTime;
    }

    public DayOfTheWeek getReminderDayOfTheWeek() {
        return reminderDayOfTheWeek;
    }

    public int getRetries() {
        return retries;
    }

    public int getRepeatIntervalInMinutes() {
        return repeatIntervalInMinutes;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public WeeklyPillRegimenRequest(String externalId, Time bestCallTime, DayOfTheWeek reminderDayOfTheWeek, int retries, int repeatIntervalInMinutes, LocalDate startDate) {
        this.externalId = externalId;
        this.bestCallTime = bestCallTime;
        this.reminderDayOfTheWeek = reminderDayOfTheWeek;
        this.retries = retries;
        this.repeatIntervalInMinutes = repeatIntervalInMinutes;
        this.startDate = startDate;
    }
}
