package org.motechproject.sms.smpp.it;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.sms.api.event.SendSmsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static org.motechproject.commons.date.util.DateUtil.now;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationSmsSmppContext.xml"})
public class SmsIT {

    @Autowired
    private EventRelay eventRelay;

    @Test
    @Ignore("run with smpp simulator; config in smpp.properties")
    public void shouldSendSms() throws Exception {
        eventRelay.sendEventMessage(new SendSmsEvent(asList("*-/*-/-!@#@"), "goo bar", now().plusMinutes(1)).toMotechEvent());
        Thread.sleep(100000000);
    }
}
