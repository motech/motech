package org.motechproject.appointments.api.model;

public enum TypeOfVisit {
    Baseline,
    Scheduled,
    Unscheduled;

    public boolean isBaselineVisit() {
        return this.equals(Baseline);
    }

    public boolean isScheduledVisit() {
        return this.equals(Scheduled);
    }
}
