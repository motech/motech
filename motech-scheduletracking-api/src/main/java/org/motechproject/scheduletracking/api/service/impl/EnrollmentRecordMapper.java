package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentRecordMapper {
    public EnrollmentRecord map(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        return new EnrollmentRecord().setExternalId(enrollment.getExternalId()).setScheduleName(enrollment.getScheduleName()).setCurrentMilestoneName(enrollment.getCurrentMilestoneName()).setPreferredAlertTime(enrollment.getPreferredAlertTime()).setReferenceDateTime(enrollment.getStartOfSchedule()).setEnrollmentDateTime(enrollment.getEnrolledOn()).setEarliestStart(null).setDueStart(null).setLateStart(null).setMaxStart(null);
    }

    public EnrollmentRecord mapWithDates(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        DateTime earliestStart = enrollment.getStartOfWindowForCurrentMilestone(WindowName.earliest);
        DateTime dueStart = enrollment.getStartOfWindowForCurrentMilestone(WindowName.due);
        DateTime lateStart = enrollment.getStartOfWindowForCurrentMilestone(WindowName.late);
        DateTime maxStart = enrollment.getStartOfWindowForCurrentMilestone(WindowName.max);
        return new EnrollmentRecord().setExternalId(enrollment.getExternalId()).setScheduleName(enrollment.getScheduleName()).setCurrentMilestoneName(enrollment.getCurrentMilestoneName()).setPreferredAlertTime(enrollment.getPreferredAlertTime()).setReferenceDateTime(enrollment.getStartOfSchedule()).setEnrollmentDateTime(enrollment.getEnrolledOn()).setEarliestStart(earliestStart).setDueStart(dueStart).setLateStart(lateStart).setMaxStart(maxStart);
    }
}
