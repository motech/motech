package org.motechproject.appointments.api.service;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.*;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.dao.AllReminderJobs;
import org.motechproject.appointments.api.mapper.ReminderMapper;
import org.motechproject.appointments.api.mapper.VisitMapper;
import org.motechproject.appointments.api.mapper.VisitResponseMapper;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AppointmentServiceImpl implements AppointmentService {
    private AllAppointmentCalendars allAppointmentCalendars;
    private AllReminderJobs allReminderJobs;

    @Autowired
    public AppointmentServiceImpl(AllAppointmentCalendars allAppointmentCalendars, AllReminderJobs allReminderJobs) {
        this.allAppointmentCalendars = allAppointmentCalendars;
        this.allReminderJobs = allReminderJobs;
    }

    public void addCalendar(AppointmentCalendarRequest appointmentCalendarRequest) {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(appointmentCalendarRequest.getExternalId());
        addVisits(appointmentCalendar, appointmentCalendarRequest.getCreateVisitRequests());
    }

    public void removeCalendar(String externalId) {
        AppointmentCalendar appointmentCalendar = allAppointmentCalendars.findByExternalId(externalId);
        if (appointmentCalendar != null) {
            allAppointmentCalendars.remove(appointmentCalendar);
        }
        allReminderJobs.removeAll(externalId);
    }

    public VisitResponse addVisit(String externalId, CreateVisitRequest createVisitRequest) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        List<VisitResponse> visitResponses = addVisits(appointmentCalendar, Arrays.asList(createVisitRequest));
        return visitResponses.get(0);
    }

    public VisitResponse findVisit(String externalId, String visitName) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        Visit visit = appointmentCalendar.getVisit(visitName);
        return new VisitResponseMapper().map(visit);
    }

    public List<VisitResponse> getAllVisits(String externalId) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        if (appointmentCalendar == null) return new ArrayList<VisitResponse>();
        List<VisitResponse> visitResponses = new ArrayList<VisitResponse>();
        for (Visit visit : appointmentCalendar.visits()) {
            visitResponses.add(new VisitResponseMapper().map(visit));
        }
        return visitResponses;
    }

    public void addCustomDataToVisit(String externalId, String visitName, Map<String, Object> data) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        Visit visit = appointmentCalendar.getVisit(visitName);
        visit.addData(data);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
    }

    public void rescheduleAppointment(String externalId, String visitName, DateTime adjustedDueDate) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        Visit visit = appointmentCalendar.getVisit(visitName);
        visit.appointment().adjustDueDate(adjustedDueDate);
        allReminderJobs.rescheduleAppointmentJob(externalId, visit);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
    }

    public void confirmAppointment(ConfirmAppointmentRequest request) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(request.getExternalId());
        Visit visit = appointmentCalendar.getVisit(request.getVisitName());
        Reminder visitReminder = new ReminderMapper().map(request.getAppointmentConfirmDate(), request.getVisitReminderConfiguration());
        visit.confirmAppointment(request.getAppointmentConfirmDate(), visitReminder);
        allReminderJobs.removeAppointmentJob(request.getExternalId(), visit);
        allReminderJobs.rescheduleVisitJob(request.getExternalId(), visit);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
    }

    public void visited(String externalId, String visitName, DateTime visitedDate) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        Visit visit = appointmentCalendar.getVisit(visitName);
        visit.visitDate(visitedDate);
        allReminderJobs.removeAll(externalId);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
    }

    public void markVisitAsMissed(String externalId, String visitName) {
        AppointmentCalendar appointmentCalendar = getAppointmentCalendar(externalId);
        Visit visit = appointmentCalendar.getVisit(visitName);
        visit.markAsMissed();
        allReminderJobs.removeAll(externalId);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
    }

    private AppointmentCalendar getAppointmentCalendar(String externalId) {
        return allAppointmentCalendars.findByExternalId(externalId);
    }

    private List<VisitResponse> addVisits(AppointmentCalendar appointmentCalendar, List<CreateVisitRequest> createVisitRequests) {
        List<VisitResponse> visitResponses = new ArrayList<VisitResponse>();
        for (CreateVisitRequest createVisitRequest : createVisitRequests) {
            Visit visit = new VisitMapper().map(createVisitRequest);
            visitResponses.add(new VisitResponseMapper().map(visit));
            appointmentCalendar.addVisit(visit);
            allReminderJobs.addAppointmentJob(appointmentCalendar.externalId(), visit);
        }
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
        return visitResponses;
    }
}
