package org.motechproject.scheduletracking.api.service.impl;

import org.motechproject.scheduletracking.api.events.EnrolledUserEvent;
import org.motechproject.scheduletracking.api.events.UnenrolledUserEvent;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;


public class EnrollmentActionService {

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    public void enroll(Map<String, Object> parameters) {
        EnrolledUserEvent enrolledUserEvent = new EnrolledUserEvent(parameters);
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest()
                .setExternalId(enrolledUserEvent.getExternalId())
                .setScheduleName(enrolledUserEvent.getScheduleName())
                .setPreferredAlertTime(enrolledUserEvent.getPreferredAlertTime())
                .setReferenceDate(enrolledUserEvent.getReferenceDate())
                .setReferenceTime(enrolledUserEvent.getReferenceTime())
                .setEnrollmentDate(enrolledUserEvent.getEnrollmentDate())
                .setEnrollmentTime(enrolledUserEvent.getEnrollmentTime())
                .setStartingMilestoneName(enrolledUserEvent.getStartingMilestoneName());

        scheduleTrackingService.enroll(enrollmentRequest);
    }

    public void unenroll(Map<String, Object> parameters) {
        UnenrolledUserEvent unenrolledUserEvent = new UnenrolledUserEvent(parameters);
        scheduleTrackingService.unenroll(unenrolledUserEvent.getExternalId(), Arrays.asList(unenrolledUserEvent.getScheduleName()));
    }
}
