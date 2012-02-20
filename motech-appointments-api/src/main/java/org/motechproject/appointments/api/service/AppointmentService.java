package org.motechproject.appointments.api.service;

import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.dao.AllReminderJobs;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AppointmentService {
    private AllAppointmentCalendars allAppointmentCalendars;
    private AllReminderJobs allReminderJobs;

    @Autowired
    public AppointmentService(AllAppointmentCalendars allAppointmentCalendars, AllReminderJobs allReminderJobs) {
        this.allAppointmentCalendars = allAppointmentCalendars;
        this.allReminderJobs = allReminderJobs;
    }

    public AppointmentCalendar getAppointmentCalendar(String externalId) {
        return allAppointmentCalendars.findByExternalId(externalId);
    }

    public void addVisit(Visit visit, String externalId) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        if (appointmentCalendar == null) {
            appointmentCalendar = new AppointmentCalendar().externalId(externalId);
        }
        appointmentCalendar.addVisit(visit);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
        allReminderJobs.add(visit.appointmentReminder(), externalId);
    }

    public void updateVisit(Visit visit, String externalId) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        appointmentCalendar.updateVisit(visit);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
    }
}
