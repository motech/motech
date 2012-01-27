package org.motechproject.scheduletracking.api.service;

import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.AlertEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.events.EnrolledEntityAlertEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
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

    @MotechListener(subjects = EventSubject.ENROLLED_ENTITY_REGULAR_ALERT)
    public void raiseAlertForEnrolledEntity(MotechEvent motechEvent) {
        EnrolledEntityAlertEvent enrolledEntityAlertEvent = new EnrolledEntityAlertEvent(motechEvent);
        String externalId = enrolledEntityAlertEvent.getExternalId();
        String scheduleName = enrolledEntityAlertEvent.getScheduleName();

        Enrollment enrollment = allEnrollments.findByExternalIdAndScheduleName(externalId, scheduleName);
        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        List<AlertEvent> alertEvents = schedule.getAlerts(enrollment.getLastFulfilledDate(), enrollment.getCurrentMilestoneName());

        for (AlertEvent alertEvent : alertEvents) {
            outboundEventGateway.sendEventMessage(new MilestoneEvent(alertEvent).toMotechEvent());
        }
    }
}
