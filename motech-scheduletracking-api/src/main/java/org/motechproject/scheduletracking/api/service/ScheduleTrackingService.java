package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import java.util.List;

public interface ScheduleTrackingService {
    String enroll(EnrollmentRequest enrollmentRequest);
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate);
    void unenroll(String externalId, String scheduleName);
    EnrollmentResponse getEnrollment(String externalId, String scheduleName);
    void safeUnEnroll(String externalId, List<String> scheduleNames);
}
