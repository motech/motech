package org.motechproject.scheduletracking.api.service;

import org.motechproject.scheduletracking.api.contract.EnrollmentRequest;

public interface ScheduleTrackingService {
    public void enroll(EnrollmentRequest enrollmentRequest);
}
