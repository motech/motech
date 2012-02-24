package org.motechproject.appointments.api.service;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.dao.AllAppointmentReminderJobs;
import org.motechproject.appointments.api.dao.AllVisitReminderJobs;
import org.motechproject.appointments.api.mapper.VisitMapper;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
        for (Integer weekOffset : appointmentCalendarRequest.getWeekOffsets()) {
            Visit visit = new VisitMapper().mapScheduledVisit(weekOffset,
                                                              appointmentCalendarRequest.getAppointmentReminderConfiguration(),
                                                              appointmentCalendarRequest.getVisitReminderConfiguration());
            appointmentCalendar.addVisit(visit);
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

    public String addVisit(String externalId, DateTime scheduledDate, ReminderConfiguration appointmentReminderConfiguration, ReminderConfiguration visitReminderConfiguration, TypeOfVisit typeOfVisit) {
        AppointmentCalendar appointmentCalendar = allAppointmentCalendars.findByExternalId(externalId);
        Visit visit = new VisitMapper().mapUnscheduledVisit(scheduledDate, appointmentReminderConfiguration, visitReminderConfiguration, typeOfVisit);
        appointmentCalendar.addVisit(visit);
        allAppointmentReminderJobs.add(visit.appointment(), externalId);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
        return visit.name();
    }

    public Appointment getAppointment(String appointmentId) {
        return allAppointmentCalendars.findAppointmentById(appointmentId);
    }

    public void confirmVisit(String externalId, String clinicVisitId, DateTime confirmedVisitDate){
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        Visit visit = appointmentCalendar.getVisit(clinicVisitId);
        if(visit.appointment().firmDate() != null){
            allVisitReminderJobs.remove(externalId);
        }
        visit.appointment().firmDate(confirmedVisitDate);
        allVisitReminderJobs.add(visit, externalId);
        updateVisit(visit, externalId);

    }
}
