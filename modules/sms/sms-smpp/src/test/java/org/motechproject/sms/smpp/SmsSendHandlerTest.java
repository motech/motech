package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SmsSendHandlerTest {

    private SmsSendHandler handler;

    @Mock
    private ManagedSmslibService managedSmslibService;

    @Before
    public void setup() {
        initMocks(this);
        handler = new SmsSendHandler(managedSmslibService);
    }

    @Test
    public void shouldListenToSmsSendEvent() throws NoSuchMethodException {
        Method handleMethod = SmsSendHandler.class.getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{EventSubjects.SEND_SMS}, annotation.subjects());
    }

    @Test
    public void shouldSendMessageUsingSmpp() throws Exception {
        handler.handle(new MotechEvent(EventSubjects.SEND_SMS, new HashMap<String, Object>() {{
            put(EventDataKeys.RECIPIENTS, Arrays.asList("0987654321"));
            put(EventDataKeys.MESSAGE, "foo bar");
        }}));
        verify(managedSmslibService).queueMessage(Arrays.asList("0987654321"), "foo bar");
    }

    @Test
    public void shouldScheduleMessageIfDeliveryTimeIsSpecified() throws Exception {
        handler.handle(new MotechEvent(EventSubjects.SEND_SMS, new HashMap<String, Object>() {{
            put(EventDataKeys.RECIPIENTS, Arrays.asList("0987654321"));
            put(EventDataKeys.MESSAGE, "foo bar");
            put(EventDataKeys.DELIVERY_TIME, new DateTime(2011, 12, 22, 4, 40, 0, 0));
        }}));
        verify(managedSmslibService).queueMessageAt(Arrays.asList("0987654321"), "foo bar", new DateTime(2011, 12, 22, 4, 40, 0, 0));
    }
}
