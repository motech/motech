package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.ExtensibleDataObject;

public class Appointment extends ExtensibleDataObject {

    @JsonProperty
    private DateTime dueDate;
    @JsonProperty
    private DateTime scheduledDate;
    @JsonProperty
    private Reminder reminder;

    public DateTime dueDate() {
        return dueDate;
    }

    public Appointment dueDate(DateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public DateTime scheduledDate() {
        return scheduledDate;
    }

    public Appointment scheduledDate(DateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
        return this;
    }

    public Reminder reminder() {
        return reminder;
    }

    public Appointment reminder(Reminder reminder) {
        this.reminder = reminder;
        return this;
    }
}
