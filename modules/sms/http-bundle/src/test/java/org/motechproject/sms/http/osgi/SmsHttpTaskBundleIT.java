package org.motechproject.sms.http.osgi;

import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.osgi.test.AbstractTaskBundleIT;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SmsHttpTaskBundleIT extends AbstractTaskBundleIT {

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.sms.api.service");
    }

    public void testTaskChannelCreated() throws IOException {
        Channel channel = findChannel("sms.http");

        assertNotNull(channel);
    }

    public void testTaskTriggers() throws IOException {
        Channel channel = findChannel("sms.http");

        assertInboundSmsTrigger(channel.getTriggerTaskEvents());
    }

    private void assertInboundSmsTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent inboundSmsTrigger = findTriggerEventBySubject(triggerTaskEvents, EventSubjects.INBOUND_SMS);

        assertNotNull(inboundSmsTrigger);
        assertTrue(hasEventParameterKey(EventDataKeys.SENDER, inboundSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.INBOUND_MESSAGE, inboundSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.TIMESTAMP, inboundSmsTrigger.getEventParameters()));
    }

    public void testTaskActions() throws IOException {
        Channel channel = findChannel("sms.http");

        assertSendSmsAction(channel.getActionTaskEvents());
    }

    private void assertSendSmsAction(List<ActionEvent> actionTaskEvents) {
        ActionEvent sendSmsAction = findActionEventBySubject(actionTaskEvents, EventSubjects.SEND_SMS);

        assertNotNull(sendSmsAction);
        assertTrue(hasActionParameterKey(EventDataKeys.RECIPIENTS, sendSmsAction.getActionParameters()));
        assertTrue(hasActionParameterKey(EventDataKeys.MESSAGE, sendSmsAction.getActionParameters()));
    }
}
