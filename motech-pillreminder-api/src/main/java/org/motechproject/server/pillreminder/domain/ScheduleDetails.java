package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class ScheduleDetails {
    private int repeatIntervalInMinutes;

    protected ScheduleDetails() {
    }

    protected ScheduleDetails(int repeatIntervalInMinutes) {
        this.repeatIntervalInMinutes = repeatIntervalInMinutes;
    }

    public int getRepeatIntervalInMinutes() {
        return repeatIntervalInMinutes;
    }

    public void setRepeatIntervalInMinutes(int repeatIntervalInMinutes) {
        this.repeatIntervalInMinutes = repeatIntervalInMinutes;
    }
}
