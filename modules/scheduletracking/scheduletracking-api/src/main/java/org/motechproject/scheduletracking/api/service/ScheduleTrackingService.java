package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.contract.UpdateCriteria;

import java.util.List;

/**
 * \ingroup sts
 *
 * Schedule Tracking Service interface
 * Provides methods to enroll an external id into a schedule, fulfill milestones in a schedule
 * and uneroll an external id from a schedule.
 * It also provides querying functionality on Enrollments using various search criteria
 *
 */
public interface ScheduleTrackingService {
    /**
     * Enrolls a user with external id into a schedule using the details in the EnrollmentRequest and schedules alerts. If the user has already enrolled for the same schedule which is currently active, then we update the existing details and reschedule alerts
     * @param enrollmentRequest
     * @return enrollmentId string
     */
    String enroll(EnrollmentRequest enrollmentRequest);

    /**
     * Fulfills the current milestone of the enrollment(with fulfillmentDate and time as the the given date and time) which belongs to the given externalId and schedule name     *
     * @param externalId
     * @param scheduleName
     * @param fulfillmentDate
     * @param fulfillmentTime
     */
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate, Time fulfillmentTime);

    /**
     * Fulfills the current milestone of the enrollment(with fulfillmentDate and time as midnight) which belongs to the given externalId and schedule name     *
     * @param externalId
     * @param scheduleName
     * @param fulfillmentDate
     */
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate);

    /**
     * Unenrolls / Removes all the scheduled jobs of enrollments which belongs to the given externalId and schedule names
     * @param externalId
     * @param scheduleNames
     */
    void unenroll(String externalId, List<String> scheduleNames);

    /**
     * Returns the EnrollmentRecord corresponds to the given externalId and schedule name
     * @param externalId
     * @param scheduleName
     * @return
     */
    EnrollmentRecord getEnrollment(String externalId, String scheduleName);

    /** Updates an active Enrollment which has the given external id and schedule name
     *
     * @param updateCriteria states the fields to be updated in the enrollment
     */
    void updateEnrollment(String externalId, String scheduleName, UpdateCriteria updateCriteria);

    /** Searches and returns the Enrollment Records as per the criteria in the given EnrollmentsQuery
     *
     * @param query
     * @return List<EnrollmentRecord>
     */
    List<EnrollmentRecord> search(EnrollmentsQuery query);

    /** Searches and returns the Enrollment Records(with all window start dates populated in them) as per the criteria in the given EnrollmentsQuery
     *
     * @param query
     * @return List<EnrollmentRecord>
     */
    List<EnrollmentRecord> searchWithWindowDates(EnrollmentsQuery query);

    /**
     * Gives the alert timings of all the windows in the milestone without actually scheduling the alert jobs
     *
     * @param enrollmentRequest
     * @return MilestoneAlerts : contains the alert timings for all the windows of the milestone
     */
    MilestoneAlerts getAlertTimings(EnrollmentRequest enrollmentRequest);

    /**
     * Saves the given schedule in database.
     *
     * @param scheduleJson : in JSON format
     */
    void add(String scheduleJson);
}
