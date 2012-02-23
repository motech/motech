package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.ExtensibleDataObject;
import org.motechproject.util.DateUtil;

public class Visit extends ExtensibleDataObject {

    @JsonProperty
    private String name;
    @JsonProperty
    private DateTime visitDate;
    @JsonProperty
    private TypeOfVisit typeOfVisit;
    @JsonProperty
    private boolean missed;
    @JsonProperty
    private Appointment appointment;
    @JsonProperty
    private Reminder reminder;
    private static final String WEEK_NUMBER = "weekNumber";

    public String name() {
        return name;
    }

    public Visit name(String name) {
        this.name = name.toLowerCase();
        return this;
    }

    public DateTime visitDate() {
        return DateUtil.setTimeZone(visitDate);
    }

    public Visit visitDate(DateTime visitDate) {
        this.visitDate = visitDate;
        return this;
    }

    public TypeOfVisit typeOfVisit() {
        return typeOfVisit;
    }

    public Visit typeOfVisit(TypeOfVisit typeOfVisit) {
        this.typeOfVisit = typeOfVisit;
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

    public Visit weekNumber(Integer weekNumber) {
        addData(WEEK_NUMBER, weekNumber);
        name("week" + weekNumber);
        return this;
    }
    public Integer weekNumber(){
        return (Integer)getData().get(WEEK_NUMBER);
    }
}
