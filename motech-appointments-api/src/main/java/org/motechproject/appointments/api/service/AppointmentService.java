package org.motechproject.appointments.api.service;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.service.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.service.contract.ConfirmAppointmentRequest;
import org.motechproject.appointments.api.service.contract.CreateVisitRequest;
import org.motechproject.appointments.api.service.contract.RescheduleAppointmentRequest;
import org.motechproject.appointments.api.service.contract.VisitsQuery;
import org.motechproject.appointments.api.service.contract.VisitResponse;

import java.util.List;
import java.util.Map;

/**
 * \defgroup appointments Appointments
 */

/**
 * \ingroup appointments
 *
 * Appointment Service exposes appointment management operations such as create appointment, find and mark as visited etc.
 *
 * @see org.motechproject.appointments.api.service.contract
 */
public interface AppointmentService {
    /**
     * Adds appointment schedules based on {@link org.motechproject.appointments.api.service.contract.AppointmentCalendarRequest AppointmentCalendarRequest} and {@link org.motechproject.appointments.api.service.contract.CreateVisitRequest CreateVisitRequest}.
     *
     * @param appointmentCalendarRequest
     */
    void addCalendar(AppointmentCalendarRequest appointmentCalendarRequest);

    /**
     * Removes all appointments and reminders for given user (identified by externalId)
     *
     * @param externalId
     */
    void removeCalendar(String externalId);

    /**
     * Adds a visit as per given {@link org.motechproject.appointments.api.service.contract.CreateVisitRequest CreateVisitRequest}
     *
     * @param externalId
     * @param createVisitRequest
     * @return
     */
    VisitResponse addVisit(String externalId, CreateVisitRequest createVisitRequest);

    /**
     * Find visit for given user identifier (external id) and visit name
     *
     * @param externalId
     * @param visitName
     * @return
     */
    VisitResponse findVisit(String externalId, String visitName);

    /**
     * Gets all scheduled visits for a given user identifier (external id)
     *
     * @param externalId
     * @return
     */
    List<VisitResponse> getAllVisits(String externalId);

    /**
     * Appends the given custom data to the existing data of a visit identified by the given visit name and user identifier (external id)
     *
     * @param externalId
     * @param visitName
     * @param data
     */
    void addCustomDataToVisit(String externalId, String visitName, Map<String, Object> data);

    /**
     * Change the due date and reminder configuration for given appointment/visit
     *
     * @param rescheduleAppointmentRequest specifies reschedule due date and reminder configuration.
     * @see org.motechproject.appointments.api.service.contract.RescheduleAppointmentRequest
     */
    void rescheduleAppointment(RescheduleAppointmentRequest rescheduleAppointmentRequest);

    /**
     * Sets confirmed visit/appointment date
     *
     * @param confirmAppointmentRequest specifies visit/appointment and confirmed date.
     * @see org.motechproject.appointments.api.service.contract.ConfirmAppointmentRequest
     */
    void confirmAppointment(ConfirmAppointmentRequest confirmAppointmentRequest);

    /**
     * Mark visit/appointment as Visited along with setting visit date and time
     *
     * @param externalId  identifies user
     * @param visitName   unique visit id for the user
     * @param visitedDate visited date
     */
    void visited(String externalId, String visitName, DateTime visitedDate);

    /**
     * Mark the visit/appointment as missed.
     *
     * @param externalId identifies user
     * @param visitName  unique visit id for the user
     */
    void markVisitAsMissed(String externalId, String visitName);

    /**
     * Returns visits filtered by the query passed in
     *
     * @param query Query by which you want to filter visits
     * @return List of matched visits
     */
    List<VisitResponse> search(VisitsQuery query);
}
