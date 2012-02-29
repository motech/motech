package org.motechproject.appointments.api.contract;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;

public class VisitResponse {

    private Visit visit;

    public VisitResponse(Visit visit) {
        this.visit = visit;
    }

    public String name() {
        return visit.name();
    }

    public DateTime visitDate() {
        return visit.visitDate();
    }

    public boolean missed() {
        return visit.missed();
    }

    public Appointment appointment() {
        return visit.appointment();
    }

    public Reminder appointmentReminder() {
        return visit.appointmentReminder();
    }

    public Reminder reminder() {
        return visit.reminder();
    }

    public Integer weekNumber(){
        return visit.weekNumber();
    }
}