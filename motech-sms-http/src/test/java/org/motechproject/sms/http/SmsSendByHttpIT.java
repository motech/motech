package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.http.service.SmsHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationSmsHttp.xml"})
public class SmsSendByHttpIT {

    private SmsSendHandler smsSendHandler;

    @Autowired
    private SmsHttpService smsHttpService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    @Ignore("use template for kookoo in sms-http-template.json")
    public void shouldSendSmsThroughKookoo() throws IOException, SmsDeliveryFailureException {
        smsSendHandler = new SmsSendHandler(smsHttpService);

        MotechEvent motechEvent = new MotechEvent(EventSubjects.SEND_SMS, new HashMap<String, Object>() {{
            put(EventDataKeys.RECIPIENTS, Arrays.asList("9686202448"));
            put(EventDataKeys.MESSAGE, "business analyst");
        }});
        smsSendHandler.handle(motechEvent);
    }
}
