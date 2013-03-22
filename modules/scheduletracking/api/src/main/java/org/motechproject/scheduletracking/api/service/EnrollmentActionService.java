package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;

public interface EnrollmentActionService {

    void enroll(String externalId, String scheduleName, String preferredAlertTime, DateTime referenceDate, String referenceTime,
                       DateTime enrolmentDate, String enrollmentTime, String startingMilestoneName);

    void unenroll(String externalId, String scheduleName);
}
