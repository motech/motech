package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.contract.UpdateCriteria;

import java.util.List;

/**
 * Schedule Tracking Service interface
 * Provides methods to enroll an external id into a schedule, fulfill milestones in a schedule
 * and uneroll an external id from a schedule.
 * It also provides querying functionality on Enrollments using various search criteria
 *
 */
public interface ScheduleTrackingService {
    /**
     * Enrolls a user with externalid into a schedule using the details in the EnrollmentRequest
     * @param enrollmentRequest
     * @return enrollmentId string
     */
    String enroll(EnrollmentRequest enrollmentRequest);

    /**
     * Fulfills the current milestone(with now as the fulfillment date time) of the enrollment which belongs to the given externalId and schedule name
     * @param externalId
     * @param scheduleName
     */
    void fulfillCurrentMilestone(String externalId, String scheduleName);

    /**
     * Fulfills the current milestone of the enrollment(with fulfillmentDate as the the given date and time as midnight) which belongs to the given externalId and schedule name
     * @param externalId
     * @param scheduleName
     * @param fulfillmentDate
     */
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate);

    /**
     * Fulfills the current milestone of the enrollment(with fulfillmentDate and time as the the given date and time) which belongs to the given externalId and schedule name     *
     * @param externalId
     * @param scheduleName
     * @param fulfillmentDate
     * @param fulfillmentTime
     */
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate, Time fulfillmentTime);

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
}
