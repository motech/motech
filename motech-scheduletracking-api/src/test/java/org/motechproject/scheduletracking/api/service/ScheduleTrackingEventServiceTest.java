package org.motechproject.scheduletracking.api.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.events.EnrolledEntityAlertEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

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
        Enrollment enrollment = mock(Enrollment.class);
        Alert alert = mock(Alert.class);
        when(enrollment.getAlerts()).thenReturn(Arrays.asList(alert));
        when(allEnrollments.get("enrollment_1")).thenReturn(enrollment);

        scheduleTrackingEventService.raiseAlertForEnrolledEntity(new EnrolledEntityAlertEvent("schedule", "enrollment_1").toMotechEvent());

        verify(outboundEventGateway).sendEventMessage(new MilestoneEvent(alert).toMotechEvent());
    }
}
