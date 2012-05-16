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
 * Custom data can be stored as key value pair in {@link CreateVisitRequest#setData(Map<String, Object>) data}  setData} map.
 */
public class CreateVisitRequest {

    private String visitName;


    private String typeOfVisit;

    private DateTime appointmentDueDate;

    private List<ReminderConfiguration> appointmentReminderConfigurations = new ArrayList<ReminderConfiguration>();

    private Map<String, Object> data = new HashMap<String, Object>();

    /**
     * Visit Name, should be unique with in a calendar
     * @return visit name uniquely identified in a calender
     */
    public String getVisitName() {
        return visitName;
    }

    public CreateVisitRequest setVisitName(String visitName) {
        this.visitName = visitName;
        return this;
    }

    /**
     * Type of visit, example : Scheduled, Ad-hoc etc
     * @return type of visit.
     */
    public String getTypeOfVisit() {
        return typeOfVisit;
    }

    public CreateVisitRequest setTypeOfVisit(String typeOfVisit) {
        this.typeOfVisit = typeOfVisit;
        return this;
    }

    /**
     * Due date for appointment, can be adjusted later on.
     * @return appointment due date
     */
    public DateTime getAppointmentDueDate() {
        return appointmentDueDate;
    }

    public CreateVisitRequest setAppointmentDueDate(DateTime appointmentDueDate) {
        this.appointmentDueDate = appointmentDueDate;
        return this;
    }

    /**
     * Additional application specific data related to clinic visit can be stored here.
     * @returns property=>value pair of application specific data
     */
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

    /**
     * Reminder Schedule template, can handle multiple reminders.
     * @return list of reminder configurations
     */
    public List<ReminderConfiguration> getAppointmentReminderConfigurations() {
        return appointmentReminderConfigurations;
    }

    public CreateVisitRequest addAppointmentReminderConfiguration(ReminderConfiguration appointmentReminderConfiguration) {
        this.appointmentReminderConfigurations.add(appointmentReminderConfiguration);
        return this;
    }
}
