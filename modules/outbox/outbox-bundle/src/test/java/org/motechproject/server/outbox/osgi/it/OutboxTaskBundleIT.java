package org.motechproject.server.outbox.osgi.it;

import org.motechproject.outbox.api.EventKeys;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.osgi.test.AbstractTaskBundleIT;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class OutboxTaskBundleIT extends AbstractTaskBundleIT {

    public void testTaskChannelCreated() throws IOException {
        Channel channel = findChannel("outbox");

        assertNotNull(channel);
    }

    public void testTaskTriggers() throws IOException {
        Channel channel = findChannel("outbox");

        assertExecuteOutboxTrigger(channel.getTriggerTaskEvents());
        assertIncompleteOutboxCallTrigger(channel.getTriggerTaskEvents());
        assertCompletedOutboxCallTrigger(channel.getTriggerTaskEvents());
        assertMaxPendingMessagesTrigger(channel.getTriggerTaskEvents());
    }

    private void assertExecuteOutboxTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent executeOutboxTrigger = findTriggerEventBySubject(triggerTaskEvents, EventKeys.EXECUTE_OUTBOX_SUBJECT);

        assertNotNull(executeOutboxTrigger);
        assertTrue(hasEventParameterKey(EventKeys.EXTERNAL_ID_KEY, executeOutboxTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventKeys.PHONE_NUMBER_KEY, executeOutboxTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventKeys.LANGUAGE_KEY, executeOutboxTrigger.getEventParameters()));
    }

    private void assertIncompleteOutboxCallTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent incompleteOutboxCallTrigger = findTriggerEventBySubject(triggerTaskEvents,
                EventKeys.INCOMPLETE_OUTBOX_CALL_SUBJECT);

        assertNotNull(incompleteOutboxCallTrigger);
        assertTrue(hasEventParameterKey(EventKeys.EXTERNAL_ID_KEY, incompleteOutboxCallTrigger.getEventParameters()));
    }

    private void assertCompletedOutboxCallTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent completeOutboxCallTrigger = findTriggerEventBySubject(triggerTaskEvents,
                EventKeys.COMPLETED_OUTBOX_CALL_SUBJECT);

        assertNotNull(completeOutboxCallTrigger);
        assertTrue(hasEventParameterKey(EventKeys.EXTERNAL_ID_KEY, completeOutboxCallTrigger.getEventParameters()));
    }

    private void assertMaxPendingMessagesTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent maxPendingMessagesTrigger = findTriggerEventBySubject(triggerTaskEvents,
                EventKeys.OUTBOX_MAX_PENDING_MESSAGES_EVENT_SUBJECT);

        assertNotNull(maxPendingMessagesTrigger);
        assertTrue(hasEventParameterKey(EventKeys.EXTERNAL_ID_KEY, maxPendingMessagesTrigger.getEventParameters()));
    }

    public void testTaskActions() throws IOException {
        Channel channel = findChannel("outbox");

        assertExecuteOutboxAction(channel.getActionTaskEvents());
        assertScheduleExecutionAction(channel.getActionTaskEvents());
        assertUnscheduleExecutionAction(channel.getActionTaskEvents());
    }

    private void assertExecuteOutboxAction(List<ActionEvent> actionTaskEvents) {
        ActionEvent executeOutboxAction = findActionEventBySubject(actionTaskEvents, EventKeys.EXECUTE_OUTBOX_SUBJECT);

        assertNotNull(executeOutboxAction);
        assertTrue(hasActionParameterKey(EventKeys.EXTERNAL_ID_KEY, executeOutboxAction.getActionParameters()));
        assertTrue(hasActionParameterKey(EventKeys.PHONE_NUMBER_KEY, executeOutboxAction.getActionParameters()));
        assertTrue(hasActionParameterKey(EventKeys.LANGUAGE_KEY, executeOutboxAction.getActionParameters()));
    }

    private void assertScheduleExecutionAction(List<ActionEvent> actionTaskEvents) {
        ActionEvent scheduleExecutionAction = findActionEventBySubject(actionTaskEvents,
                EventKeys.SCHEDULE_EXECUTION_SUBJECT);

        assertNotNull(scheduleExecutionAction);
        assertTrue(hasActionParameterKey(EventKeys.CALL_HOUR_KEY, scheduleExecutionAction.getActionParameters()));
        assertTrue(hasActionParameterKey(EventKeys.CALL_MINUTE_KEY, scheduleExecutionAction.getActionParameters()));
        assertTrue(hasActionParameterKey(EventKeys.EXTERNAL_ID_KEY, scheduleExecutionAction.getActionParameters()));
        assertTrue(hasActionParameterKey(EventKeys.PHONE_NUMBER_KEY, scheduleExecutionAction.getActionParameters()));
        assertTrue(hasActionParameterKey(EventKeys.LANGUAGE_KEY, scheduleExecutionAction.getActionParameters()));
    }

    private void assertUnscheduleExecutionAction(List<ActionEvent> actionTaskEvents) {
        ActionEvent unscheduleExecutionAction = findActionEventBySubject(actionTaskEvents,
                EventKeys.UNSCHEDULE_EXECUTION_SUBJECT);

        assertNotNull(unscheduleExecutionAction);
        assertTrue(hasActionParameterKey(EventKeys.SCHEDULE_JOB_ID_KEY, unscheduleExecutionAction.getActionParameters()));
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testOutboxBundleContext.xml"};
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.outbox.api.service");
    }
}
