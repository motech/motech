package org.motechproject.appointments.api.contract;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateVisitRequest {

    private String visitName;

    private String typeOfVisit;

    private DateTime appointmentDueDate;

    private List<ReminderConfiguration> appointmentReminderConfigurations;

    private Map<String, Object> data = new HashMap<String, Object>();

    public String getVisitName() {
        return visitName;
    }

    public CreateVisitRequest setVisitName(String visitName) {
        this.visitName = visitName;
        return this;
    }

    public String getTypeOfVisit() {
        return typeOfVisit;
    }

    public CreateVisitRequest setTypeOfVisit(String typeOfVisit) {
        this.typeOfVisit = typeOfVisit;
        return this;
    }

    public DateTime getAppointmentDueDate() {
        return appointmentDueDate;
    }

    public CreateVisitRequest setAppointmentDueDate(DateTime appointmentDueDate) {
        this.appointmentDueDate = appointmentDueDate;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public CreateVisitRequest setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public CreateVisitRequest addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public ReminderConfiguration getAppointmentReminderConfiguration() {
        if (appointmentReminderConfigurations != null && !appointmentReminderConfigurations.isEmpty())
            return appointmentReminderConfigurations.get(0);
        return null;
    }

    @Deprecated
    public CreateVisitRequest setAppointmentReminderConfiguration(ReminderConfiguration appointmentReminderConfiguration) {
        if (appointmentReminderConfigurations == null) {
            appointmentReminderConfigurations = new ArrayList<ReminderConfiguration>();
        }
        this.appointmentReminderConfigurations.add(appointmentReminderConfiguration);
        return this;
    }

    public CreateVisitRequest addAppointmentReminderConfiguration(ReminderConfiguration appointmentReminderConfiguration) {
        if (appointmentReminderConfigurations == null) {
            appointmentReminderConfigurations = new ArrayList<ReminderConfiguration>();
        }
        this.appointmentReminderConfigurations.add(appointmentReminderConfiguration);
        return this;
    }

    public List<ReminderConfiguration> getAppointmentReminderConfigurations() {
        return appointmentReminderConfigurations;
    }
}
