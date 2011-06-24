package org.motechproject.server.pillreminder.domain;

import java.util.Date;

public class Reminder {
    private Date dateTime;

    public Reminder() {
    }

    public Reminder(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        if (dateTime != null ? !dateTime.equals(reminder.dateTime) : reminder.dateTime != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return dateTime != null ? dateTime.hashCode() : 0;
    }
}
