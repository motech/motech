package org.motechproject.scheduletracking.api.osgi;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.osgi.test.AbstractTaskBundleIT;

import java.io.IOException;
import java.util.List;

public class ScheduleTrackingTaskBundleIT extends AbstractTaskBundleIT {

    public void testTaskChannelCreated() throws IOException {
        Channel channel = findChannel("scheduleTracking");

        assertNotNull(channel);
    }

    public void testTaskTriggers() throws IOException {
        Channel channel = findChannel("scheduleTracking");

        assertMileStoneAlertTrigger(channel.getTriggerTaskEvents());
        assertDefaultmentCaptureTrigger(channel.getTriggerTaskEvents());
    }

    private void assertMileStoneAlertTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent milestoneAlertTrigger = findTriggerEventBySubject(triggerTaskEvents, EventSubjects.MILESTONE_ALERT);

        assertNotNull(milestoneAlertTrigger);
        assertTrue(hasEventParameterKey(EventDataKeys.WINDOW_NAME, milestoneAlertTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.MILESTONE_NAME, milestoneAlertTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.EARLIEST_DATE_TIME, milestoneAlertTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.DUE_DATE_TIME, milestoneAlertTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.LATE_DATE_TIME, milestoneAlertTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.DEFAULTMENT_DATE_TIME, milestoneAlertTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.SCHEDULE_NAME, milestoneAlertTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.EXTERNAL_ID, milestoneAlertTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.REFERENCE_DATE, milestoneAlertTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.MILESTONE_DATA, milestoneAlertTrigger.getEventParameters()));
    }

    private void assertDefaultmentCaptureTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent defaultmentCaptureTrigger = findTriggerEventBySubject(triggerTaskEvents,
                EventSubjects.DEFAULTMENT_CAPTURE);

        assertNotNull(defaultmentCaptureTrigger);
        assertTrue(hasEventParameterKey(EventDataKeys.ENROLLMENT_ID, defaultmentCaptureTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.EXTERNAL_ID, defaultmentCaptureTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(MotechSchedulerService.JOB_ID_KEY,
                defaultmentCaptureTrigger.getEventParameters()));
    }

    public void testTaskActions() throws IOException {
        Channel channel = findChannel("scheduleTracking");

        assertDefaultCaptureAction(channel.getActionTaskEvents());
    }

    private void assertDefaultCaptureAction(List<ActionEvent> actionTaskEvents) {
        ActionEvent defaultmentCaptureAction = findActionEventBySubject(actionTaskEvents, EventSubjects.DEFAULTMENT_CAPTURE);

        assertNotNull(defaultmentCaptureAction);
        assertTrue(hasActionParameterKey(EventDataKeys.ENROLLMENT_ID, defaultmentCaptureAction.getActionParameters()));
        assertTrue(hasActionParameterKey(EventDataKeys.EXTERNAL_ID, defaultmentCaptureAction.getActionParameters()));
        assertTrue(hasActionParameterKey(MotechSchedulerService.JOB_ID_KEY,
                defaultmentCaptureAction.getActionParameters()));

    }
}
