package org.motechproject.scheduletracking.api.service;

import org.motechproject.scheduletracking.api.domain.MilestoneNotPartOfScheduleException;

public interface ScheduleTrackingService {
    void enroll(EnrollmentRequest enrollmentRequest) throws MilestoneNotPartOfScheduleException;
}
