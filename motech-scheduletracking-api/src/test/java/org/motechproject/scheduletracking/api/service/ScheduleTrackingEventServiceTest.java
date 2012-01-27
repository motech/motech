package org.motechproject.scheduletracking.api.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.EnrolledEntityAlertEvent;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;

public class ScheduleTrackingEventServiceTest {
    @Mock
    private AllTrackedSchedules allTrackedSchedules;
    @Mock
    private AllEnrollments allEnrollments;
    @Mock
    private OutboundEventGateway outboundEventGateway;

    ScheduleTrackingEventService scheduleTrackingEventService;

    @Before
    public void setup() {
        initMocks(this);
        scheduleTrackingEventService = new ScheduleTrackingEventService(allTrackedSchedules, allEnrollments, outboundEventGateway);
    }

    @Test
    public void shouldRaiseMilestoneEvent() {
        String firstMilestoneName = "First Milestone";
        String scheduleName = "scheduleName";
        String externalId = "externalId";

        Milestone firstMilestone = new Milestone(firstMilestoneName, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        firstMilestone.getMilestoneWindow(WindowName.Due).addAlert(new AlertConfiguration(null, wallTimeOf(1), 3));
        Enrollment enrollment = new Enrollment(externalId, scheduleName, firstMilestoneName, weeksAgo(2), weeksAgo(3));
        Schedule schedule = new Schedule(scheduleName, new WallTime(10, WallTimeUnit.Week), firstMilestone);

        when(allEnrollments.findByExternalIdAndScheduleName(externalId, scheduleName)).thenReturn(enrollment);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        MotechEvent motechEvent = new EnrolledEntityAlertEvent(scheduleName, externalId).toMotechEvent();
        scheduleTrackingEventService.raiseAlertForEnrolledEntity(motechEvent);

        ArgumentCaptor<MotechEvent> milestoneEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundEventGateway).sendEventMessage(milestoneEventArgumentCaptor.capture());
    }
}
