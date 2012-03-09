package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentRecordMapper {

    private EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentRecordMapper(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    public EnrollmentRecord map(Enrollment enrollment) {
        if (enrollment == null)
            return null;
        return new EnrollmentRecord(enrollment.getExternalId(), enrollment.getScheduleName(), enrollment.getCurrentMilestoneName(), enrollment.getPreferredAlertTime(), enrollment.getReferenceDateTime(), enrollment.getEnrollmentDateTime(), null, null, null, null);
    }

    public EnrollmentRecord mapWithDates(Enrollment enrollment) {
        if (enrollment == null)
            return null;
        DateTime earliestStart = enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.earliest);
        DateTime dueStart = enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.due);
        DateTime lateStart = enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.late);
        DateTime maxStart = enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.max);
        return new EnrollmentRecord(enrollment.getExternalId(), enrollment.getScheduleName(), enrollment.getCurrentMilestoneName(), enrollment.getPreferredAlertTime(), enrollment.getReferenceDateTime(), enrollment.getEnrollmentDateTime(), earliestStart, dueStart, lateStart, maxStart);
    }
}
