package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.CronJobExpressionBuilder;
import org.motechproject.scheduletracking.api.contract.EnrollmentRequest;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleTrackingException;
import org.motechproject.scheduletracking.api.events.EnrolledEntityAlertEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.joda.time.LocalDate.now;

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
        List<Enrollment> enrollments = allEnrollments.findByExternalIdAndScheduleName(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName());
        if (!enrollments.isEmpty()) return;

        Schedule schedule = allTrackedSchedules.get(enrollmentRequest.getScheduleName());
        if (schedule == null) {
            throw new ScheduleTrackingException("No schedule with name: %s", enrollmentRequest.getScheduleName());
        }

	    Enrollment enrollment = new Enrollment(enrollmentRequest.getExternalId(), now(), schedule.getName(), schedule.getFirstMilestone().getName());
        allEnrollments.add(enrollment);

        MotechEvent motechEvent = new EnrolledEntityAlertEvent(schedule.getName(), enrollment.getId()).toMotechEvent();
        String cronJobExpression = new CronJobExpressionBuilder(enrollmentRequest.preferredAlertTime(), 24, 0).build();
	    LocalDate startDate = enrollmentRequest.getReferenceDate();
	    CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, startDate.toDate(), schedule.getEndDate(startDate).toDate());
        schedulerService.scheduleJob(schedulableJob);
    }
}
