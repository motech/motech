package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

import java.util.List;

public interface ScheduleTrackingService {
    String enroll(EnrollmentRequest enrollmentRequest);
    void fulfillCurrentMilestone(String externalId, String scheduleName);
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate);
    void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate, Time fulfillmentTime);
    void unenroll(String externalId, List<String> scheduleNames);
    EnrollmentResponse getEnrollment(String externalId, String scheduleName);

    List<String> findExternalIds(EnrollmentsQuery query);
}
