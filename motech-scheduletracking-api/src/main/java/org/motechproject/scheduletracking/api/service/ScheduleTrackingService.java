package org.motechproject.scheduletracking.api.service;

import org.motechproject.scheduletracking.api.contract.EnrollmentRequest;

public interface ScheduleTrackingService {
    void enroll(EnrollmentRequest enrollmentRequest);
}
