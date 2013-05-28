package org.motechproject.sms.http;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.testing.utils.BaseUnitTest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.constants.EventDataKeys.FAILURE_COUNT;
import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENTS;

public class SmsFailureHandlerTest extends BaseUnitTest {
    private static final DateTime SEND_TIME = new DateTime(2013, 6, 15, 11, 25);

    @Mock
    private EventRelay eventRelay;

    @Mock
    private SettingsFacade settingsFacade;

    private SmsFailureHandler handler;

    @Before
    public void setUp() {
        initMocks(this);

        mockCurrentDate(SEND_TIME);
        when(settingsFacade.getProperty("max_retries")).thenReturn("5");

        handler = new SmsFailureHandler(eventRelay, settingsFacade);
    }

    @Test
    public void shouldListenToSmsFailureEvent() throws NoSuchMethodException {
        Method handleMethod = SmsFailureHandler.class.getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{EventSubjects.SMS_FAILURE_NOTIFICATION}, annotation.subjects());
    }

    @Test
    public void shouldRaiseSendSmsEventWhenFailureCountIsLessThanMaxRetries() throws SmsDeliveryFailureException {
        String recipient = "recipient";
        String messageText = "message_text";
        int failureCount = 1;

        Map<String, Object> param = new HashMap<>();
        param.put(EventDataKeys.RECIPIENT, recipient);
        param.put(EventDataKeys.MESSAGE, messageText);
        param.put(EventDataKeys.FAILURE_COUNT, failureCount);

        handler.handle(new MotechEvent(EventSubjects.SMS_FAILURE_NOTIFICATION, param));

        Map<String, Object> eventParam = new HashMap<>();
        eventParam.put(RECIPIENTS, Arrays.asList(recipient));
        eventParam.put(MESSAGE, messageText);
        eventParam.put(FAILURE_COUNT, failureCount);

        verify(eventRelay).sendEventMessage(new MotechEvent(EventSubjects.SEND_SMS, eventParam));
    }

    @Test
    public void shouldNotRaiseSendSmsEventWhenFailureCountIsGreaterThanMaxRetries() throws SmsDeliveryFailureException {
        String recipient = "recipient";
        String messageText = "message_text";
        int failureCount = 6;

        Map<String, Object> param = new HashMap<>();
        param.put(EventDataKeys.RECIPIENT, recipient);
        param.put(EventDataKeys.MESSAGE, messageText);
        param.put(EventDataKeys.FAILURE_COUNT, failureCount);

        handler.handle(new MotechEvent(EventSubjects.SMS_FAILURE_NOTIFICATION, param));

        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }

}
