package org.motechproject.scheduletracking.api.service;

import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.dao.AllEnrolments;
import org.motechproject.scheduletracking.api.dao.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.domain.enrolment.Enrolment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.events.EnrolledEntityAlertEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTrackingEventService {
    private AllTrackedSchedules allTrackedSchedules;
    private AllEnrolments allEnrolments;
    private OutboundEventGateway outboundEventGateway;

    @Autowired
    public ScheduleTrackingEventService(AllTrackedSchedules allTrackedSchedules, AllEnrolments allEnrolments, OutboundEventGateway outboundEventGateway) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.allEnrolments = allEnrolments;
        this.outboundEventGateway = outboundEventGateway;
    }

    @MotechListener(subjects = {EventKeys.ENROLLED_ENTITY_REGULAR_ALERT})
    public void raiseAlertForEnrolledEntity(MotechEvent motechEvent) {
        EnrolledEntityAlertEvent enrolledEntityAlertEvent = new EnrolledEntityAlertEvent(motechEvent);
        Schedule schedule = allTrackedSchedules.get(enrolledEntityAlertEvent.scheduleName());
        Enrolment enrolment = allEnrolments.get(enrolledEntityAlertEvent.externalId());
        Alert alert = schedule.alertFor(enrolment);
        if (alert != null) {
            MilestoneEvent milestoneEvent = new MilestoneEvent(alert);
            outboundEventGateway.sendEventMessage(milestoneEvent.toMotechEvent());
        }
    }
}
