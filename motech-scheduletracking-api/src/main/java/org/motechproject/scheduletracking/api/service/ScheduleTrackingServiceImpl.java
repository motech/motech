package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.contract.EnrollmentRequest;
import org.motechproject.scheduletracking.api.dao.AllEnrollments;
import org.motechproject.scheduletracking.api.dao.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleTrackingException;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.scheduletracking.api.events.EnrolledEntityAlertEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ScheduleTrackingServiceImpl implements ScheduleTrackingService {
    @Autowired
    private AllTrackedSchedules allTrackedSchedules;
    @Autowired
    private MotechSchedulerService schedulerService;
    @Autowired
    private AllEnrollments allEnrollments;

    @Override
    public void enroll(EnrollmentRequest enrollmentRequest) {
        List<Enrollment> found = allEnrollments.findByExternalIdAndScheduleName(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName());
        if (found.size() > 0) return;

        Schedule schedule = allTrackedSchedules.get(enrollmentRequest.getScheduleName());
        if (schedule == null) {
            throw new ScheduleTrackingException("No schedule with name: %s", enrollmentRequest.getScheduleName());
        }

        Enrollment enrollment = schedule.newEnrollment(enrollmentRequest.getExternalId(), LocalDate.now());
        allEnrollments.add(enrollment);

        MotechEvent motechEvent = new EnrolledEntityAlertEvent(schedule.getName(), enrollment.getId()).toMotechEvent();
        String cronJobExpression = new CronJobExpressionBuilder(enrollmentRequest.preferredAlertTime(), 24, 0).build();
        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, new Date(), schedule.endDate());
        schedulerService.scheduleJob(schedulableJob);
    }
}
