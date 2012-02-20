package org.motechproject.scheduletracking.api.service;

public interface ScheduleTrackingService {
    String enroll(EnrollmentRequest enrollmentRequest);
    void fulfillCurrentMilestone(String externalId, String scheduleName);
    void unenroll(String externalId, String scheduleName);
    EnrollmentResponse getEnrollment(String externalId, String scheduleName);
}
