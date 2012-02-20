package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.ExtensibleDataObject;
import org.motechproject.util.DateUtil;

import java.util.UUID;

public class Visit extends ExtensibleDataObject {

    @JsonProperty
    private String id;
    @JsonProperty
    private DateTime visitDate;
    @JsonProperty
    private String title;
    @JsonProperty
    private boolean missed;
    @JsonProperty
    private Appointment appointment;
    @JsonProperty
    private Reminder reminder;

    public Visit() {
        this.id = UUID.randomUUID().toString();
    }

    public String id() {
        return id;
    }

    public DateTime visitDate() {
        return visitDate == null ? null : DateUtil.setTimeZone(visitDate);
    }

    public Visit visitDate(DateTime visitDate) {
        this.visitDate = visitDate;
        return this;
    }

    public String title() {
        return title;
    }

    public Visit title(String title) {
        this.title = title;
        return this;
    }

    public boolean missed() {
        return missed;
    }

    public Visit missed(boolean missed) {
        this.missed = missed;
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

    public Reminder appointmentReminder() {
        return appointment == null ? null : appointment.reminder();
    }

    public Visit reminder(Reminder reminder) {
        this.reminder = reminder;
        return this;
    }
}
