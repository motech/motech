package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
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
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class SmsSendHandlerTest {

    private SmsSendHandler handler;
    @Mock
    private HttpClient httpClient;
    @Mock
    private TemplateReader templateReader;

    private SmsSendTemplate template;
    private HttpMethod httpMethod;

    @Before
    public void setUp() {
        initMocks(this);

        template = mock(SmsSendTemplate.class);
        httpMethod = new GetMethod();
        when(template.generateRequestFor(Arrays.asList("0987654321"), "foo bar")).thenReturn(httpMethod);
        when(templateReader.getTemplate(null)).thenReturn(template);
        handler = new SmsSendHandler(templateReader, httpClient);
        setField(handler, "template", template);
        setField(handler, "commonsHttpClient", httpClient);
    }

    @Test
    public void shouldListenToSmsSendEvent() throws NoSuchMethodException {
        Method handleMethod = SmsSendHandler.class.getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue(handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{SEND_SMS}, annotation.subjects());
    }

    @Test
    public void shouldMakeRequest() throws IOException {
        MotechEvent event = new MotechEvent(SEND_SMS, new HashMap<String, Object>() {{
            put(RECIPIENTS, Arrays.asList("0987654321"));
            put(MESSAGE, "foo bar");
        }});
        handler.handle(event);

        verify(httpClient).executeMethod(httpMethod);
    }
}
