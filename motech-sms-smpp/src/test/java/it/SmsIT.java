package it;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.constants.EventSubject;
import org.motechproject.sms.api.service.SmsServiceImpl;
import org.motechproject.sms.smpp.ManagedSmslibService;
import org.motechproject.sms.smpp.SmsSendHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationSmsSmpp.xml"})
public class SmsIT {

    private SmsSendHandler smsSendHandler;

    @Autowired
    private ManagedSmslibService service;

    @Test
    @Ignore("run with smpp simulator; config in smpp.properties")
    public void shouldSendSms() throws Exception {
        smsSendHandler = new SmsSendHandler(service);

        MotechEvent event = new MotechEvent(EventSubject.SEND_SMS, new HashMap<String, Object>() {{
            put(SmsServiceImpl.RECIPIENTS, Arrays.asList("*-/*-/-!@#@"));
            put(SmsServiceImpl.MESSAGE, "goo bar");
            put(SmsServiceImpl.DELIVERY_TIME, DateTime.now().plusMinutes(1));
        }});
        smsSendHandler.handle(event);
	    Thread.sleep(100000000);
    }
}
