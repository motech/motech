package org.motechproject.sms.smpp.osgi;

import org.motechproject.sms.smpp.constants.EventSubjects;
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
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENTS;
import static org.motechproject.sms.api.constants.EventDataKeys.SENDER;
import static org.motechproject.sms.api.constants.EventDataKeys.TIMESTAMP;
import static org.motechproject.sms.api.constants.EventSubjects.SEND_SMS;
import static org.motechproject.sms.smpp.constants.EventDataKeys.RECIPIENT;
import static org.motechproject.sms.smpp.constants.EventDataKeys.STATUS_MESSAGE;

public class SmsSmppTaskBundleIT extends AbstractTaskBundleIT {

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.sms.api.service");
    }

    public void testTaskChannelCreated() throws IOException {
        Channel channel = findChannel("sms.smpp");

        assertNotNull(channel);
    }

    public void testTaskTriggers() throws IOException {
        Channel channel = findChannel("sms.smpp");

        assertSmsFailureNotificationTrigger(channel.getTriggerTaskEvents());
        assertInboundSmsTrigger(channel.getTriggerTaskEvents());
        assertSmsDeliveryReport(channel.getTriggerTaskEvents());
    }

    private void assertSmsFailureNotificationTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent smsFailureNotificationTrigger = findTriggerEventBySubject(triggerTaskEvents,
                EventSubjects.SMS_FAILURE_NOTIFICATION);

        assertNotNull(smsFailureNotificationTrigger);
        assertTrue(hasEventParameterKey(RECIPIENT, smsFailureNotificationTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(MESSAGE, smsFailureNotificationTrigger.getEventParameters()));
    }

    private void assertInboundSmsTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent inboundSmsTrigger = findTriggerEventBySubject(triggerTaskEvents, EventSubjects.INBOUND_SMS);

        assertNotNull(inboundSmsTrigger);
        assertTrue(hasEventParameterKey(SENDER, inboundSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(INBOUND_MESSAGE, inboundSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(TIMESTAMP, inboundSmsTrigger.getEventParameters()));
    }

    private void assertSmsDeliveryReport(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent smsDeliveryReportTrigger = findTriggerEventBySubject(triggerTaskEvents,
                EventSubjects.SMS_DELIVERY_REPORT);

        assertNotNull(smsDeliveryReportTrigger);
        assertTrue(hasEventParameterKey(SENDER, smsDeliveryReportTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(STATUS_MESSAGE, smsDeliveryReportTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(TIMESTAMP, smsDeliveryReportTrigger.getEventParameters()));
    }

    public void testTaskActions() throws IOException {
        Channel channel = findChannel("sms.smpp");

        assertSendSmsAction(channel.getActionTaskEvents());
    }

    private void assertSendSmsAction(List<ActionEvent> actionTaskEvents) {
        ActionEvent sendSmsAction = findActionEventBySubject(actionTaskEvents, SEND_SMS);

        assertNotNull(sendSmsAction);
        assertTrue(hasActionParameterKey(RECIPIENTS, sendSmsAction.getActionParameters()));
        assertTrue(hasActionParameterKey(MESSAGE, sendSmsAction.getActionParameters()));
        assertTrue(hasActionParameterKey(DELIVERY_TIME, sendSmsAction.getActionParameters()));
    }

}
