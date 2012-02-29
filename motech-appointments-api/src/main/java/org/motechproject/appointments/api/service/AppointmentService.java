package org.motechproject.appointments.api.service;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.springframework.stereotype.Component;

@Component
public interface AppointmentService {
    public void addCalendar(AppointmentCalendarRequest appointmentCalendarRequest);
    public void removeCalendar(String externalId);
    public void updateVisit(Visit visit, String externalId);
    public AppointmentCalendar getAppointmentCalendar(String externalId);
    public VisitResponse addVisit(String externalId, String visitName, VisitRequest visitRequest);
    public Appointment getAppointment(String appointmentId);
    public VisitResponse findVisit(String externalId, String visitName);
    public void confirmVisit(String externalId, String clinicVisitId, DateTime confirmedVisitDate, ReminderConfiguration visitReminderConfiguration);
    public void setVisitDate(String externalId, String visitId, DateTime visitDate);
}
