package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentRecordMapper {
    public EnrollmentRecord map(Enrollment enrollment) {
        if (enrollment == null)
            return null;
        return new EnrollmentRecord(enrollment.getExternalId(), enrollment.getScheduleName(), enrollment.getCurrentMilestoneName(), enrollment.getPreferredAlertTime(), enrollment.getStartOfSchedule(), enrollment.getEnrolledOn(), null, null, null, null);
    }

    public EnrollmentRecord mapWithDates(Enrollment enrollment) {
        if (enrollment == null)
            return null;
        DateTime earliestStart = enrollment.getStartOfWindowForCurrentMilestone(WindowName.earliest);
        DateTime dueStart = enrollment.getStartOfWindowForCurrentMilestone(WindowName.due);
        DateTime lateStart = enrollment.getStartOfWindowForCurrentMilestone(WindowName.late);
        DateTime maxStart = enrollment.getStartOfWindowForCurrentMilestone(WindowName.max);
        return new EnrollmentRecord(enrollment.getExternalId(), enrollment.getScheduleName(), enrollment.getCurrentMilestoneName(), enrollment.getPreferredAlertTime(), enrollment.getStartOfSchedule(), enrollment.getEnrolledOn(), earliestStart, dueStart, lateStart, maxStart);
    }
}
