package org.motechproject.sms.http;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.http.service.SmsHttpService;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SmsSendHandlerTest {

    @Mock
    private SmsHttpService smsHttpService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldListenToSmsSendEvent() throws NoSuchMethodException {
        Method handleMethod = SmsSendHandler.class.getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{EventSubjects.SEND_SMS}, annotation.subjects());
    }

    @Test
    public void shouldSendSmsBasedOnEventDetails() throws SmsDeliveryFailureException {
        HashMap<String, Object> eventParameters = new HashMap<String, Object>();
        List<String> recipients = asList(new String[]{"recipient_1", "recipient_2"});
        String messageText = "message_text";

        eventParameters.put(EventDataKeys.RECIPIENTS, recipients);
        eventParameters.put(EventDataKeys.MESSAGE, messageText);

        MotechEvent motechEvent = new MotechEvent(EventSubjects.SEND_SMS, eventParameters);

        new SmsSendHandler(smsHttpService).handle(motechEvent);
        verify(smsHttpService).sendSms(recipients, messageText);
    }
}
