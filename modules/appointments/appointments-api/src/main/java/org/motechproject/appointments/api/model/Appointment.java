package org.motechproject.appointments.api.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.ExtensibleDataObject;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Appointment extends ExtensibleDataObject<Appointment> {

    @JsonProperty
    private String id;
    @JsonProperty
    private DateTime originalDueDate;
    @JsonProperty
    private DateTime dueDate;
    @JsonProperty
    private DateTime confirmedDate;
    @JsonProperty
    private List<Reminder> reminders = new ArrayList<Reminder>();

    public Appointment() {
        id = UUID.randomUUID().toString();
    }

    public String id() {
        return id;
    }

    public DateTime dueDate() {
        return DateUtil.setTimeZone(dueDate);
    }

    public Appointment dueDate(DateTime due) {
        this.dueDate = due;
        this.originalDueDate = due;
        return this;
    }

    public DateTime originalDueDate() {
        return DateUtil.setTimeZone(originalDueDate);
    }

    public Appointment adjustDueDate(DateTime adjustedDueDate, List<Reminder> reminders) {
        this.dueDate = adjustedDueDate;
        this.reminders = reminders;
        return this;
    }

    public DateTime confirmedDate() {
        return DateUtil.setTimeZone(confirmedDate);
    }

    public Appointment confirmedDate(DateTime confirmed) {
        this.confirmedDate = confirmed;
        return this;
    }

    public List<Reminder> reminders() {
        return reminders;
    }

    public Appointment reminders(List<Reminder> reminders) {
        this.reminders = reminders;
        return this;
    }

    public boolean isSame(Appointment appointment) {
        return new EqualsBuilder()
                .append(getData(), appointment.getData())
                .append(dueDate, appointment.dueDate())
                .append(confirmedDate, appointment.confirmedDate())
                .append(originalDueDate, appointment.originalDueDate())
                .append(reminders, appointment.reminders())
                .isEquals();
    }
}
