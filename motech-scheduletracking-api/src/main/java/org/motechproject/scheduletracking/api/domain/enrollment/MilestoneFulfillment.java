package org.motechproject.scheduletracking.api.domain.enrollment;

import org.joda.time.LocalDate;

public class MilestoneFulfillment {
    private LocalDate dateFulfilled;

    private MilestoneFulfillment() {
    }

    public MilestoneFulfillment(LocalDate dateFulfilled) {
        this.dateFulfilled = dateFulfilled;
    }

    public LocalDate getDateFulfilled() {
        return dateFulfilled;
    }

    public void setDateFulfilled(LocalDate dateFulfilled) {
        this.dateFulfilled = dateFulfilled;
    }
}
