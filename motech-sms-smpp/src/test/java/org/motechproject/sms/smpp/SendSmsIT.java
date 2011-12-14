package org.motechproject.sms.smpp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationSmsSmpp.xml"})
public class SendSmsIT {

    private SmsSendHandler smsSendHandler;

    @Autowired
    private ManagedSmslibService service;

    @Test
    public void shouldSendSms() throws Exception {
        smsSendHandler = new SmsSendHandler(service);

        MotechEvent event = new MotechEvent(SmsService.SEND_SMS, new HashMap<String, Object>() {{
            put(SmsService.RECIPIENTS, Arrays.asList("123"));
            put(SmsService.MESSAGE, "goo bar");
        }});
        smsSendHandler.handle(event);
    }
}
