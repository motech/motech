package org.motechproject.appointments.api.service;

import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.dao.AllReminderJobs;
import org.motechproject.appointments.api.mapper.VisitMapper;
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

    public void addCalendar(AppointmentCalendarRequest appointmentCalendarRequest) {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(appointmentCalendarRequest.getExternalId());
        for (Integer weekOffset : appointmentCalendarRequest.getWeekOffsets()) {
            Visit visit = new VisitMapper().mapScheduledVisit(weekOffset, appointmentCalendarRequest.getReminderConfiguration());
            appointmentCalendar.addVisit(visit);
            allReminderJobs.add(visit.appointmentReminder(), appointmentCalendarRequest.getExternalId());
        }
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
    }

    public void removeCalendar(String externalId) {
        AppointmentCalendar appointmentCalendar = allAppointmentCalendars.findByExternalId(externalId);
        if(appointmentCalendar != null){
            allReminderJobs.remove(appointmentCalendar.externalId());
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
}
