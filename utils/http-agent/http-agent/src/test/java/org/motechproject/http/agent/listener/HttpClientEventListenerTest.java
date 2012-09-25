package org.motechproject.http.agent.listener;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.http.agent.domain.EventDataKeys;
import org.motechproject.http.agent.domain.EventSubjects;
import org.motechproject.http.agent.domain.Method;
import org.motechproject.event.MotechEvent;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpClientEventListenerTest {

    @Mock
    private RestTemplate restTempate;

    @Mock
    HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory;
    @Mock
    SettingsFacade settings;

    private HttpClientEventListener httpClientEventListener;

    @Before
    public void setup() {
        when(settings.getProperty(HttpClientEventListener.HTTP_CONNECT_TIMEOUT)).thenReturn("0");
        when(settings.getProperty(HttpClientEventListener.HTTP_READ_TIMEOUT)).thenReturn("0");
        when(restTempate.getRequestFactory()).thenReturn(httpComponentsClientHttpRequestFactory);
        httpClientEventListener = new HttpClientEventListener(restTempate, settings);
    }

    @Test
    public void shouldReadFromQueueAndMakeAHttpCallForPost() throws IOException {
        final String postUrl = "http://commcare";
        final String postData = "aragorn";
        MotechEvent motechEvent = new MotechEvent(EventSubjects.HTTP_REQUEST, new HashMap<String, Object>() {{
            put(EventDataKeys.URL, postUrl);
            put(EventDataKeys.DATA, postData);
            put(EventDataKeys.METHOD, Method.POST);
        }});

        httpClientEventListener.handle(motechEvent);

        verify(restTempate).postForLocation(postUrl, postData);
    }

    @Test
    public void shouldReadFromQueueAndMakeAHttpCall() throws IOException {
        final String putUrl = "http://commcare";
        final String postData = "aragorn";
        MotechEvent motechEvent = new MotechEvent(EventSubjects.HTTP_REQUEST, new HashMap<String, Object>() {{
            put(EventDataKeys.URL, putUrl);
            put(EventDataKeys.DATA, postData);
            put(EventDataKeys.METHOD, Method.PUT);
        }});

        httpClientEventListener.handle(motechEvent);

        verify(restTempate).put(putUrl, postData);
    }

    @Test
    public void shouldReadFromQueueAndMakeAHttpDeleteCall() throws IOException {
        final String deleteUrl = "http://commcare";
        final String deleteRequest = "aragorn";
        MotechEvent motechEvent = new MotechEvent(EventSubjects.HTTP_REQUEST, new HashMap<String, Object>() {{
            put(EventDataKeys.URL, deleteUrl);
            put(EventDataKeys.DATA, deleteRequest);
            put(EventDataKeys.METHOD, Method.DELETE);
        }});

        httpClientEventListener.handle(motechEvent);

        verify(restTempate).delete(deleteUrl, deleteRequest);
    }
}
