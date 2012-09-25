package org.motechproject.http.agent.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.http.agent.components.AsynchronousCall;
import org.motechproject.http.agent.components.SynchronousCall;
import org.motechproject.http.agent.domain.EventDataKeys;
import org.motechproject.http.agent.domain.Method;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
public class HttpClientServiceImplTest {

    @Mock
    private AsynchronousCall asynchronousCall;
    @Mock
    private SynchronousCall synchronousCall;

    HttpAgent httpAgent;

    @Before
    public void setup() {
        initMocks(this);
        httpAgent = new HttpAgentImpl(asynchronousCall, synchronousCall);
    }

    @Test
    public void shouldExecutePostRequest() {
        String url = "someurl";
        String data = "data";
        httpAgent.execute(url, data, Method.POST);

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(asynchronousCall).send(motechEventArgumentCaptor.capture());
        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();

        assertEquals(Method.POST, eventMessageSent.getParameters().get(EventDataKeys.METHOD));
        assertEquals(data, (String) eventMessageSent.getParameters().get(EventDataKeys.DATA));
        assertEquals(url, eventMessageSent.getParameters().get(EventDataKeys.URL));
    }

    @Test
    public void shouldExecutePutRequest() {
        String url = "someurl";
        String data = "data";
        httpAgent.execute(url, data, Method.PUT);

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(asynchronousCall).send(motechEventArgumentCaptor.capture());
        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();

        assertEquals(Method.PUT, eventMessageSent.getParameters().get(EventDataKeys.METHOD));
        assertEquals(data, (String) eventMessageSent.getParameters().get(EventDataKeys.DATA));
        assertEquals(url, eventMessageSent.getParameters().get(EventDataKeys.URL));
    }

    @Test
    public void shouldExecuteDeleteRequest() {
        String url = "someurl";
        String data = "data";
        httpAgent.execute(url, data, Method.DELETE);

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(asynchronousCall).send(motechEventArgumentCaptor.capture());
        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();

        assertEquals(Method.DELETE, eventMessageSent.getParameters().get(EventDataKeys.METHOD));
        assertEquals(data, (String) eventMessageSent.getParameters().get(EventDataKeys.DATA));
        assertEquals(url, eventMessageSent.getParameters().get(EventDataKeys.URL));
    }

    @Test
    public void shouldExecuteSynchronousCalls(){
        String url = "someurl";
        String data = "data";

        httpAgent.executeSync(url, data, Method.POST);

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(synchronousCall).send(motechEventArgumentCaptor.capture());
        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();

        assertEquals(Method.POST, eventMessageSent.getParameters().get(EventDataKeys.METHOD));
        assertEquals(data, (String) eventMessageSent.getParameters().get(EventDataKeys.DATA));
        assertEquals(url, eventMessageSent.getParameters().get(EventDataKeys.URL));
    }
}
