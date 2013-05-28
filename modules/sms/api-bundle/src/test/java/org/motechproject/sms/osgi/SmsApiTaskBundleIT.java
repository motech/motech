package org.motechproject.sms.osgi;

import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.osgi.test.AbstractTaskBundleIT;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.motechproject.sms.api.constants.EventDataKeys.DELIVERY_TIME;
import static org.motechproject.sms.api.constants.EventDataKeys.INBOUND_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENT;
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENTS;
import static org.motechproject.sms.api.constants.EventDataKeys.SENDER;
import static org.motechproject.sms.api.constants.EventDataKeys.TIMESTAMP;
import static org.motechproject.sms.api.constants.EventSubjects.SEND_SMS;
import static org.motechproject.sms.api.constants.EventSubjects.SMS_FAILURE_NOTIFICATION;

public class SmsApiTaskBundleIT extends AbstractTaskBundleIT {

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.sms.api.service");
    }

    public void testTaskChannelCreated() throws IOException {
        Channel channel = findChannel("sms.api");

        assertNotNull(channel);
    }

    public void testTaskTriggers() throws IOException {
        Channel channel = findChannel("sms.api");

        assertSendSmsTrigger(channel.getTriggerTaskEvents());
        assertInboundSmsTrigger(channel.getTriggerTaskEvents());
        assertSmsFailureNotificationTrigger(channel.getTriggerTaskEvents());
    }

    public void testTaskActions() throws IOException {
        Channel channel = findChannel("sms.api");

        assertSendSmsAction(channel.getActionTaskEvents());
    }

    private void assertSendSmsTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent sendSmsTrigger = findTriggerEventBySubject(triggerTaskEvents, EventSubjects.SEND_SMS);

        assertNotNull(sendSmsTrigger);
        assertTrue(hasEventParameterKey(EventDataKeys.RECIPIENTS, sendSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.MESSAGE, sendSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.DELIVERY_TIME, sendSmsTrigger.getEventParameters()));
    }

    private void assertInboundSmsTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent inboundSmsTrigger = findTriggerEventBySubject(triggerTaskEvents, EventSubjects.INBOUND_SMS);

        assertNotNull(inboundSmsTrigger);
        assertTrue(hasEventParameterKey(SENDER, inboundSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(INBOUND_MESSAGE, inboundSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(TIMESTAMP, inboundSmsTrigger.getEventParameters()));
    }

    private void assertSendSmsAction(List<ActionEvent> actionTaskEvents) {
        ActionEvent sendSmsAction = findActionEventBySubject(actionTaskEvents, SEND_SMS);

        assertNotNull(sendSmsAction);
        assertTrue(hasActionParameterKey(RECIPIENTS, sendSmsAction.getActionParameters()));
        assertTrue(hasActionParameterKey(MESSAGE, sendSmsAction.getActionParameters()));
        assertTrue(hasActionParameterKey(DELIVERY_TIME, sendSmsAction.getActionParameters()));
    }

    private void assertSmsFailureNotificationTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent smsFailureNotificationTrigger = findTriggerEventBySubject(triggerTaskEvents,
                SMS_FAILURE_NOTIFICATION);

        assertNotNull(smsFailureNotificationTrigger);
        assertTrue(hasEventParameterKey(RECIPIENT, smsFailureNotificationTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(MESSAGE, smsFailureNotificationTrigger.getEventParameters()));
    }
}
