package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.exception.NoMoreMilestonesToFulfillException;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.Completed;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.Unenrolled;
import static org.motechproject.util.StringUtil.isNullOrEmpty;

@Component
public class EnrollmentService {
    private AllTrackedSchedules allTrackedSchedules;
    private AllEnrollments allEnrollments;
    private EnrollmentAlertService enrollmentAlertService;
    private EnrollmentDefaultmentService enrollmentDefaultmentService;

    @Autowired
    public EnrollmentService(AllTrackedSchedules allTrackedSchedules, AllEnrollments allEnrollments, EnrollmentAlertService enrollmentAlertService, EnrollmentDefaultmentService enrollmentDefaultmentService) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.allEnrollments = allEnrollments;
        this.enrollmentAlertService = enrollmentAlertService;
        this.enrollmentDefaultmentService = enrollmentDefaultmentService;
    }

    public String enroll(String externalId, String scheduleName, String startingMilestoneName, DateTime referenceDateTime, DateTime enrollmentDateTime, Time preferredAlertTime) {
        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        EnrollmentStatus enrollmentStatus = EnrollmentStatus.Active;
        if (schedule.hasExpiredBy(referenceDateTime))
            enrollmentStatus = EnrollmentStatus.Defaulted;

        Enrollment enrollment = allEnrollments.addOrReplace(new Enrollment(externalId, scheduleName, startingMilestoneName, referenceDateTime, enrollmentDateTime, preferredAlertTime, enrollmentStatus));
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);
        enrollmentDefaultmentService.scheduleJobToCaptureDefaultment(enrollment);

        return enrollment.getId();
    }

    public void fulfillCurrentMilestone(Enrollment enrollment, DateTime fulfillmentDateTime) {
        Schedule schedule = allTrackedSchedules.getByName(enrollment.getScheduleName());
        if (isNullOrEmpty(enrollment.getCurrentMilestoneName()))
            throw new NoMoreMilestonesToFulfillException();

        unscheduleJobs(enrollment);

        enrollment.fulfillCurrentMilestone(fulfillmentDateTime);
        String nextMilestoneName = schedule.getNextMilestoneName(enrollment.getCurrentMilestoneName());
        enrollment.setCurrentMilestoneName(nextMilestoneName);
        if (nextMilestoneName == null)
            enrollment.setStatus(Completed);
        else
            scheduleJobs(enrollment);

        allEnrollments.update(enrollment);
    }

    public void unenroll(Enrollment enrollment) {
        unscheduleJobs(enrollment);
        enrollment.setStatus(Unenrolled);
        allEnrollments.update(enrollment);
    }

    private void scheduleJobs(Enrollment enrollment) {
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);
        enrollmentDefaultmentService.scheduleJobToCaptureDefaultment(enrollment);
    }

    private void unscheduleJobs(Enrollment enrollment) {
        enrollmentAlertService.unscheduleAllAlerts(enrollment);
        enrollmentDefaultmentService.unscheduleDefaultmentCaptureJob(enrollment);
    }
}
