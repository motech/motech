package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.contract.UpdateCriteria;

import java.util.List;

public interface ScheduleTrackingService {
    String enroll(EnrollmentRequest enrollmentRequest);
    void fulfillCurrentMilestone(String externalId, String scheduleName);
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate);
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate, Time fulfillmentTime);
    void unenroll(String externalId, List<String> scheduleNames);
    EnrollmentRecord getEnrollment(String externalId, String scheduleName);

    /** Updates an active Enrollment which has the given external id and schedule name
     *
     * @param updateCriteria states the fields to be updated in the enrollment
     */
    void updateEnrollment(String externalId, String scheduleName, UpdateCriteria updateCriteria);
    List<EnrollmentRecord> search(EnrollmentsQuery query);
    List<EnrollmentRecord> searchWithWindowDates(EnrollmentsQuery query);
}
