package org.motechproject.sms.smpp.it;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.context.EventContext;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.event.EventRelay;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationSmsSmpp.xml"})
public class SmsIT {

    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

    @Test
    @Ignore("run with smpp simulator; config in smpp.properties")
    public void shouldSendSms() throws Exception {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put(EventDataKeys.RECIPIENTS, Arrays.asList("*-/*-/-!@#@"));
        data.put(EventDataKeys.MESSAGE, "goo bar");
        data.put(EventDataKeys.DELIVERY_TIME, DateTime.now().plusMinutes(1));

        eventRelay.sendEventMessage(new MotechEvent(EventSubjects.SEND_SMS, data));
        Thread.sleep(100000000);
    }
}