package org.motechproject.sms.osgi;

import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.osgi.test.AbstractTaskBundleIT;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    }

    private void assertSendSmsTrigger(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent sendSmsTrigger = findTriggerEventBySubject(triggerTaskEvents, EventSubjects.SEND_SMS);

        assertNotNull(sendSmsTrigger);
        assertTrue(hasEventParameterKey(EventDataKeys.RECIPIENTS, sendSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.MESSAGE, sendSmsTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(EventDataKeys.DELIVERY_TIME, sendSmsTrigger.getEventParameters()));
    }
}
