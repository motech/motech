package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.ExtensibleDataObject;

public class Appointment extends ExtensibleDataObject {

    @JsonProperty
    private DateTime dueDate;
    @JsonProperty
    private DateTime firmDate;
    @JsonProperty
    private Reminder reminder;

    public DateTime dueDate() {
        return dueDate;
    }

    public Appointment dueDate(DateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public DateTime firmDate() {
        return firmDate;
    }

    public Appointment firmDate(DateTime firmDate) {
        this.firmDate = firmDate;
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
