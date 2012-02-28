package org.motechproject.appointments.api.service;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.dao.AllAppointmentReminderJobs;
import org.motechproject.appointments.api.dao.AllVisitReminderJobs;
import org.motechproject.appointments.api.mapper.ReminderMapper;
import org.motechproject.appointments.api.mapper.VisitMapper;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AppointmentService {
    private AllAppointmentCalendars allAppointmentCalendars;
    private AllAppointmentReminderJobs allAppointmentReminderJobs;
    private AllVisitReminderJobs allVisitReminderJobs;

    @Autowired
    public AppointmentService(AllAppointmentCalendars allAppointmentCalendars,
                              AllAppointmentReminderJobs allAppointmentReminderJobs,
                              AllVisitReminderJobs allVisitReminderJobs) {
        this.allAppointmentCalendars = allAppointmentCalendars;
        this.allAppointmentReminderJobs = allAppointmentReminderJobs;
        this.allVisitReminderJobs = allVisitReminderJobs;
    }

    public void addCalendar(AppointmentCalendarRequest appointmentCalendarRequest) {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(appointmentCalendarRequest.getExternalId());
        Map<String, VisitRequest> visits = appointmentCalendarRequest.getVisitRequests();
        for (String visitName : visits.keySet()) {
            VisitRequest visitRequest = visits.get(visitName);
            Visit visit = new VisitMapper().map(visitName, visitRequest);
            appointmentCalendar.addVisit(visit);
            if (visit.appointment() != null)
                allAppointmentReminderJobs.add(visit.appointment(), appointmentCalendarRequest.getExternalId());
        }
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
    }

    public void removeCalendar(String externalId) {
        AppointmentCalendar appointmentCalendar = allAppointmentCalendars.findByExternalId(externalId);
        if (appointmentCalendar != null) {
            allAppointmentReminderJobs.remove(appointmentCalendar.externalId());
            allAppointmentCalendars.remove(appointmentCalendar);
        }
    }

    public void updateVisit(Visit visit, String externalId) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        appointmentCalendar.updateVisit(visit);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
    }

    public AppointmentCalendar getAppointmentCalendar(String externalId) {
        return allAppointmentCalendars.findByExternalId(externalId);
    }

    public String addVisit(String externalId, String visitName, VisitRequest visitRequest) {
        AppointmentCalendar appointmentCalendar = allAppointmentCalendars.findByExternalId(externalId);
        Visit visit = new VisitMapper().map(visitName, visitRequest);
        appointmentCalendar.addVisit(visit);
        if (visit.appointment() != null) {
            allAppointmentReminderJobs.add(visit.appointment(), externalId);
        }
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
        return visit.name();
    }

    public Appointment getAppointment(String appointmentId) {
        return allAppointmentCalendars.findAppointmentById(appointmentId);
    }

    public Visit findVisit(String externalId, String visitName) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        return appointmentCalendar.getVisit(visitName);
    }

    public void confirmVisit(String externalId, String clinicVisitId, DateTime confirmedVisitDate, ReminderConfiguration visitReminderConfiguration) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        Visit visit = appointmentCalendar.getVisit(clinicVisitId);
        if (visit.appointment().confirmedDate() != null) {
            allVisitReminderJobs.remove(externalId);
        }
        visit.appointment().confirmedDate(confirmedVisitDate);
        Reminder visitReminder = new ReminderMapper().map(confirmedVisitDate, visitReminderConfiguration);
        visit.reminder(visitReminder);
        allVisitReminderJobs.add(visit, externalId);
        updateVisit(visit, externalId);
    }

    public void setVisitDate(String externalId, String visitId, DateTime visitDate) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        Visit visit = appointmentCalendar.getVisit(visitId);
        allVisitReminderJobs.remove(externalId);
        visit.visitDate(visitDate);
        updateVisit(visit, externalId);
    }
}
