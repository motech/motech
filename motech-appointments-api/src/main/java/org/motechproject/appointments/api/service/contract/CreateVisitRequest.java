package org.motechproject.appointments.api.service.contract;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * \ingroup Appointments
 *
 * Used as configuration for creating appointment / scheduled visit.
 * Reminders can be configured by {@link ReminderConfiguration}
 * Custom data can be stored as key value pair in {@link CreateVisitRequest#data} map.
 */
public class CreateVisitRequest {

    /**
     * Visit Name, should be unique with in a calendar
     */
    private String visitName;

    /**
     * Type of visit, example : Scheduled, Ad-hoc etc
     */
    private String typeOfVisit;

    /**
     * Due date for appointment, can be adjusted later on.
     */
    private DateTime appointmentDueDate;

    /**
     * Reminder Schedule template, can handle multiple reminders.
     */
    private List<ReminderConfiguration> appointmentReminderConfigurations = new ArrayList<ReminderConfiguration>();

    /**
     * Additional application specific data related to clinic visit can be stored here.
     */
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

    public List<ReminderConfiguration> getAppointmentReminderConfigurations() {
        return appointmentReminderConfigurations;
    }

    public CreateVisitRequest addAppointmentReminderConfiguration(ReminderConfiguration appointmentReminderConfiguration) {
        this.appointmentReminderConfigurations.add(appointmentReminderConfiguration);
        return this;
    }
}
