package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.service.SmsService.*;
import static org.motechproject.sms.http.SmsSendTemplate.Response;

public class SmsSendHandlerTest {

    @Mock
    private HttpClient httpClient;
    @Mock
    private TemplateReader templateReader;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldListenToSmsSendEvent() throws NoSuchMethodException {
        Method handleMethod = SmsSendHandler.class.getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{SEND_SMS}, annotation.subjects());
    }

    @Test
    public void shouldMakeRequest() throws IOException, SmsDeliveryFailureException {
        SmsSendTemplate template = mock(SmsSendTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(template.generateRequestFor(Arrays.asList("0987654321"), "foo bar")).thenReturn(httpMethod);
        when(templateReader.getTemplate(anyString())).thenReturn(template);

        SmsSendHandler handler = new SmsSendHandler(templateReader, httpClient);
        handler.handle(new MotechEvent(SEND_SMS, new HashMap<String, Object>() {{
            put(RECIPIENTS, Arrays.asList("0987654321"));
            put(MESSAGE, "foo bar");
        }}));

        verify(httpClient).executeMethod(httpMethod);
    }

    @Test
    public void shouldNotThrowExceptionIfResponseIsASuccess() throws IOException, SmsDeliveryFailureException {
        Response response = new Response();
        response.success = "sent";
        SmsSendTemplate template = mock(SmsSendTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(httpMethod.getResponseBodyAsString()).thenReturn("sent");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(template.getResponse()).thenReturn(response);
        when(templateReader.getTemplate(Matchers.<String>any())).thenReturn(template);

        SmsSendHandler handler = new SmsSendHandler(templateReader, httpClient);
        handler.handle(new MotechEvent(SEND_SMS));
    }

    @Test(expected = SmsDeliveryFailureException.class)
    public void shouldThrowExceptionIfResponseIsNotASuccess() throws IOException, SmsDeliveryFailureException {
        SmsSendTemplate template = mock(SmsSendTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);
        Response response = new Response();
        response.success = "sent";

        when(httpMethod.getResponseBodyAsString()).thenReturn("boom");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(template.getResponse()).thenReturn(response);
        when(templateReader.getTemplate(Matchers.<String>any())).thenReturn(template);

        SmsSendHandler handler = new SmsSendHandler(templateReader, httpClient);
        handler.handle(new MotechEvent(SEND_SMS));
    }
}
