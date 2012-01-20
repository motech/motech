package org.motechproject.scheduletracking.api.service;

import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.events.EnrolledEntityAlertEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleTrackingEventService {
    private AllTrackedSchedules allTrackedSchedules;
    private AllEnrollments allEnrollments;
    private OutboundEventGateway outboundEventGateway;

    @Autowired
    public ScheduleTrackingEventService(AllTrackedSchedules allTrackedSchedules, AllEnrollments allEnrollments, OutboundEventGateway outboundEventGateway) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.allEnrollments = allEnrollments;
        this.outboundEventGateway = outboundEventGateway;
    }

    @MotechListener(subjects = {EventDataKeys.ENROLLED_ENTITY_REGULAR_ALERT})
    public void raiseAlertForEnrolledEntity(MotechEvent motechEvent) {
        EnrolledEntityAlertEvent enrolledEntityAlertEvent = new EnrolledEntityAlertEvent(motechEvent);
        Schedule schedule = allTrackedSchedules.get(enrolledEntityAlertEvent.scheduleName());
        Enrollment enrollment = allEnrollments.get(enrolledEntityAlertEvent.enrollmentId());
        List<Alert> alerts = enrollment.getAlerts();
        for (Alert alert : alerts) {
            outboundEventGateway.sendEventMessage(new MilestoneEvent(alert).toMotechEvent());
        }
    }
}
