package org.motechproject.scheduletracking.api.service;

import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.contract.EnrolmentRequest;
import org.motechproject.scheduletracking.api.dao.AllEnrolments;
import org.motechproject.scheduletracking.api.dao.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.domain.enrolment.Enrolment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleTrackingException;
import org.motechproject.scheduletracking.api.domain.factory.EnrolmentFactory;
import org.motechproject.scheduletracking.api.events.EnrolledEntityAlertEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduleTrackingServiceImpl implements ScheduleTrackingService {
    @Autowired
    private AllTrackedSchedules allTrackedSchedules;
    @Autowired
    private MotechSchedulerService schedulerService;
    @Autowired
    private AllEnrolments allEnrolments;

    @Override
    public void enrol(EnrolmentRequest enrolmentRequest) {
        Schedule schedule = allTrackedSchedules.get(enrolmentRequest.scheduleName());
        if (schedule == null) throw new ScheduleTrackingException("No schedule with name: %s", enrolmentRequest.scheduleName());

        Enrolment enrolment = EnrolmentFactory.newEnrolment(enrolmentRequest);
        allEnrolments.add(enrolment);

        MotechEvent motechEvent = new EnrolledEntityAlertEvent(schedule.name(), enrolment.getId()).toMotechEvent();
        String cronJobExpression = new CronJobExpressionBuilder(enrolmentRequest.preferredAlertTime(), 24, 0).build();
        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, new Date(), schedule.endDate());
        schedulerService.scheduleJob(schedulableJob);
    }
}
