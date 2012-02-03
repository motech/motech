package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class MilestoneFulfillment implements Serializable {
    private String milestoneName;
    private LocalDate dateFulfilled;

    private MilestoneFulfillment() {
    }

    public MilestoneFulfillment(String milestoneName, LocalDate dateFulfilled) {
        this.milestoneName = milestoneName;
        this.dateFulfilled = dateFulfilled;
    }

    public LocalDate getDateFulfilled() {
        return dateFulfilled;
    }

    public void setDateFulfilled(LocalDate dateFulfilled) {
        this.dateFulfilled = dateFulfilled;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }
}
