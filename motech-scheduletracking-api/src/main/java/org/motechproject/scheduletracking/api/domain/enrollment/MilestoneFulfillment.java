package org.motechproject.scheduletracking.api.domain.enrollment;

import org.joda.time.LocalDate;

public class MilestoneFulfillment {
    private LocalDate date;

    private MilestoneFulfillment() {
    }

    public MilestoneFulfillment(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
