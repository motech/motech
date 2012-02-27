package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;

public interface ScheduleTrackingService {
    String enroll(EnrollmentRequest enrollmentRequest);
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate);
    void unenroll(String externalId, String scheduleName);
    EnrollmentResponse getEnrollment(String externalId, String scheduleName);
}
