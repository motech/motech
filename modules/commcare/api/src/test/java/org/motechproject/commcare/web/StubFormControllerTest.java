package org.motechproject.commcare.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StubFormControllerTest {

    @Mock
    private EventRelay eventRelay;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private StubFormController stubController = new StubFormController(eventRelay);

    private String json = "{\"received_on\":\"1-1-2012\",\"form_id\":\"id123\",\"case_ids\":[\"123\",\"345\"]}";

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIncomingFormStubJsonFailure() throws IOException {

        ArgumentCaptor<MotechEvent> argumentCall = ArgumentCaptor.forClass(MotechEvent.class);

        stubController.receiveFormEvent("test", response);
        verify(eventRelay, times(1)).sendEventMessage(argumentCall.capture());

        MotechEvent event = argumentCall.getValue();

        assertEquals(event.getSubject(), EventSubjects.FORM_STUB_FAIL_EVENT);
    }

    @Test
    public void testIncomingFormStubJsonSuccess() throws IOException {

        ArgumentCaptor<MotechEvent> argumentCall = ArgumentCaptor.forClass(MotechEvent.class);

        stubController.receiveFormEvent(json, response);
        verify(eventRelay, times(1)).sendEventMessage(argumentCall.capture());

        MotechEvent event = argumentCall.getValue();

        assertEquals(event.getSubject(), EventSubjects.FORM_STUB_EVENT);

        Map<String, Object> eventParameters = event.getParameters();

        assertEquals(eventParameters.size(), 3);
        assertEquals(eventParameters.get(EventDataKeys.FORM_ID), "id123");
        assertEquals(((List<String>) eventParameters.get(EventDataKeys.CASE_IDS)).size(), 2);
        assertEquals(eventParameters.get(EventDataKeys.RECEIVED_ON), "1-1-2012");
    }
}
