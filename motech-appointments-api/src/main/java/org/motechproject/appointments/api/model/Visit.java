package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.ExtensibleDataObject;
import org.motechproject.util.DateUtil;

public class Visit extends ExtensibleDataObject {

    @JsonProperty
    private String name;
    @JsonProperty
    private String typeOfVisit;
    @JsonProperty
    private DateTime visitDate;
    @JsonProperty
    private boolean missed;
    @JsonProperty
    private Appointment appointment;
    @JsonProperty
    private Reminder reminder;

    public String name() {
        return name;
    }

    public Visit name(String name) {
        this.name = name;
        return this;
    }

    public String typeOfVisit() {
        return typeOfVisit;
    }

    public Visit typeOfVisit(String typeOfVisit) {
        this.typeOfVisit = typeOfVisit;
        return this;
    }

    public DateTime visitDate() {
        return DateUtil.setTimeZone(visitDate);
    }

    public Visit visitDate(DateTime visitDate) {
        this.visitDate = visitDate;
        return this;
    }

    public boolean missed() {
        return missed;
    }

    public Visit markAsMissed() {
        missed = true;
        return this;
    }

    public Appointment appointment() {
        return appointment;
    }

    public Visit appointment(Appointment appointment) {
        this.appointment = appointment;
        return this;
    }

    public Reminder reminder() {
        return reminder;
    }

    public Visit reminder(Reminder reminder) {
        this.reminder = reminder;
        return this;
    }

    public Reminder appointmentReminder() {
        return appointment == null ? null : appointment.reminder();
    }

    public Visit addAppointment(DateTime dueDate, Reminder reminder) {
        this.appointment = new Appointment().dueDate(dueDate).reminder(reminder);
        return this;
    }

    public void confirmAppointment(DateTime appointmentConfirmationDate, Reminder visitReminder) {
        appointment().confirmedDate(appointmentConfirmationDate);
        reminder(visitReminder);
    }
}
