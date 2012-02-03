package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleTrackingException;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTrackingServiceImpl implements ScheduleTrackingService {
    private AllTrackedSchedules allTrackedSchedules;
    private AllEnrollments allEnrollments;
    private EnrollmentService enrollmentService;

    @Autowired
    public ScheduleTrackingServiceImpl(AllTrackedSchedules allTrackedSchedules, AllEnrollments allEnrollments, EnrollmentService enrollmentService) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.allEnrollments = allEnrollments;
        this.enrollmentService = enrollmentService;
    }

    @Override
    public void enroll(EnrollmentRequest enrollmentRequest) {
        String externalId = enrollmentRequest.getExternalId();
        String scheduleName = enrollmentRequest.getScheduleName();
        LocalDate referenceDate = enrollmentRequest.getReferenceDate();

        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        if (schedule == null) {
            throw new ScheduleTrackingException("No schedule with name: %s", scheduleName);
        }

        Enrollment enrollment;
        if (enrollmentRequest.enrollIntoMilestone())
            enrollment = new Enrollment(externalId, schedule, referenceDate, DateUtil.today(), enrollmentRequest.getPreferredAlertTime(), enrollmentRequest.getStartingMilestoneName());
        else
            enrollment = new Enrollment(externalId, schedule, referenceDate, DateUtil.today(), enrollmentRequest.getPreferredAlertTime());
        allEnrollments.addOrReplace(enrollment);
        enrollmentService.scheduleAlertsForCurrentMilestone(enrollment);
    }

    @Override
    public void fulfillCurrentMilestone(String externalId, String scheduleName) {
        enrollmentService.fulfillCurrentMilestone(allEnrollments.findByExternalIdAndScheduleName(externalId, scheduleName));
    }
}
