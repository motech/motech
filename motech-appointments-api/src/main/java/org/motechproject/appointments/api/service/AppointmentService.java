package org.motechproject.appointments.api.service;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.service.contract.*;

import java.util.List;
import java.util.Map;

public interface AppointmentService {
    public void addCalendar(AppointmentCalendarRequest appointmentCalendarRequest);

    public void removeCalendar(String externalId);

    public VisitResponse addVisit(String externalId, CreateVisitRequest createVisitRequest);

    public VisitResponse findVisit(String externalId, String visitName);

    public List<VisitResponse> getAllVisits(String externalId);

    public void addCustomDataToVisit(String externalId, String visitName, Map<String, Object> data);

    public void rescheduleAppointment(RescheduleAppointmentRequest rescheduleAppointmentRequest);

    public void confirmAppointment(ConfirmAppointmentRequest confirmAppointmentRequest);

    public void visited(String externalId, String visitName, DateTime visitedDate);

    public void markVisitAsMissed(String externalId, String visitName);

    /** Returns visits filtered by the query passed in
     *
     * @param query Query by which you want to filter visits
     * @return  List of matched visits
     */
    public List<VisitResponse> search(VisitsQuery query);
}
