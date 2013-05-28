package org.motechproject.sms.smpp.osgi;

import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.osgi.test.AbstractTaskBundleIT;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.motechproject.sms.api.constants.EventDataKeys.SENDER;
import static org.motechproject.sms.api.constants.EventDataKeys.STATUS_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.TIMESTAMP;
import static org.motechproject.sms.api.constants.EventSubjects.SMS_DELIVERY_REPORT;

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

        assertSmsDeliveryReport(channel.getTriggerTaskEvents());
    }

    private void assertSmsDeliveryReport(List<TriggerEvent> triggerTaskEvents) {
        TriggerEvent smsDeliveryReportTrigger = findTriggerEventBySubject(triggerTaskEvents,
                SMS_DELIVERY_REPORT);

        assertNotNull(smsDeliveryReportTrigger);
        assertTrue(hasEventParameterKey(SENDER, smsDeliveryReportTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(STATUS_MESSAGE, smsDeliveryReportTrigger.getEventParameters()));
        assertTrue(hasEventParameterKey(TIMESTAMP, smsDeliveryReportTrigger.getEventParameters()));
    }
}
