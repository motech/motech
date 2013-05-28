package org.motechproject.sms.http.it;

import org.apache.commons.httpclient.HttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.http.TemplateReader;
import org.motechproject.sms.http.service.SmsHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class SmsSendByHttpIT {
    @Autowired
    private HttpClient commonsHttpClient;

    @Autowired
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    private EventRelay eventRelay;

    private SmsHttpService smsHttpService;

    @Test
    @Ignore("Test for Kookoo")
    public void shouldSendSmsThroughKookoo() throws IOException, SmsDeliveryFailureException {
        TemplateReader templateReader = new TemplateReader("/templates/sample-kookoo-template.json");
        smsHttpService = new SmsHttpService(commonsHttpClient, motechSchedulerService, templateReader, eventRelay);
        smsHttpService.sendSms(Arrays.asList("9686202448"), "business analyst");
    }

    @Test
    @Ignore("Test for Voxeo")
    public void shouldSendSmsThroughVoxeo() throws IOException, SmsDeliveryFailureException {
        TemplateReader templateReader = new TemplateReader("/templates/sample-voxeo-template.json");
        smsHttpService = new SmsHttpService(commonsHttpClient, motechSchedulerService, templateReader, eventRelay);
        smsHttpService.sendSms(Arrays.asList("017732345337"), "Test Voxeo");
    }
}
