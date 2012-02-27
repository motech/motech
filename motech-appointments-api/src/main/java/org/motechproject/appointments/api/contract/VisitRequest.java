package org.motechproject.appointments.api.contract;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class VisitRequest {

    private DateTime dueDate;

    private ReminderConfiguration reminderConfiguration;

    private Map<String, Object> data = new HashMap<String, Object>();

    public DateTime getDueDate() {
        return dueDate;
    }

    public VisitRequest setDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public VisitRequest setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public VisitRequest addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public ReminderConfiguration getReminderConfiguration() {
        return reminderConfiguration;
    }

    public VisitRequest setReminderConfiguration(ReminderConfiguration reminderConfiguration) {
        this.reminderConfiguration = reminderConfiguration;
        return this;
    }
}
