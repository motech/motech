package org.motechproject.commcare.web;

import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commcare.events.constants.EventDataKeys.*;
import static org.motechproject.commcare.events.constants.EventSubjects.FORMS_EVENT;
import static org.motechproject.commcare.events.constants.EventSubjects.FORMS_FAIL_EVENT;

public class FullFormControllerTest {

    @Mock
    private EventRelay eventRelay;

    private FullFormController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        controller = new FullFormController(eventRelay);
    }

    @Test
    public void testIncomingFormsFailure() {
        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        controller.receiveForm("");
        verify(eventRelay).sendEventMessage(captor.capture());

        MotechEvent event = captor.getValue();

        assertEquals(event.getSubject(), FORMS_FAIL_EVENT);
    }

    @Test
    public void testIncomingFormsSuccess() {
        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        controller.receiveForm(getBody());
        verify(eventRelay).sendEventMessage(captor.capture());

        MotechEvent event = captor.getValue();
        assertEquals(event.getSubject(), FORMS_EVENT);

        Map<String, Object> parameters = event.getParameters();
        assertTrue(parameters.containsKey(ATTRIBUTES));
        assertTrue(parameters.containsKey(SUB_ELEMENTS));
        assertEquals("data", parameters.get(VALUE));
        assertEquals("form", parameters.get(ELEMENT_NAME));

        Map<String, String> attributes = (Map<String, String>) parameters.get(ATTRIBUTES);
        assertEquals(4, attributes.size());
        assertEquals("1", attributes.get("uiVersion"));
        assertEquals("41", attributes.get("version"));
        assertEquals("New Form", attributes.get("name"));
        assertEquals("http://openrosa.org/formdesigner/84FA38A2-93C1-4B9E-AA2A-0E082995FF9E", attributes.get("xmlns"));

        Multimap<String, Object> subElements = (Multimap<String, Object>) parameters.get(SUB_ELEMENTS);
        assertEquals(5, subElements.size());

        assertHasKeys(subElements, "number", "case", "cc_delegation_stub", "meta");

        List numberElements = new ArrayList(subElements.get("number"));
        assertEquals(2, numberElements.size());
        ArrayList numberElementsList = new ArrayList(numberElements);

        assertEquals("8", ((Map<String, Object>) numberElementsList.get(0)).get(EventDataKeys.VALUE));
        assertEquals("9", ((Map<String, Object>) numberElementsList.get(1)).get(EventDataKeys.VALUE));
    }



    private void assertHasKeys(Multimap<String, Object> map, String... keys) {
        for(String key: keys) {
            assertTrue(map.containsKey(key));
        }
    }

    private String getBody() {
        return "<data uiVersion=\"1\"\n" +
                "      version=\"41\"\n" +
                "      name=\"New Form\"\n" +
                "      xmlns:jrm=\"http://dev.commcarehq.org/jr/xforms\"\n" +
                "      xmlns=\"http://openrosa.org/formdesigner/84FA38A2-93C1-4B9E-AA2A-0E082995FF9E\">\n" +
                "  <number>8</number>\n" +
                "  <number>9</number>\n" +
                "  <n0:case case_id=\"e098a110-6b83-4ff7-9093-d8e0e8bfb9a3\"\n" +
                "           user_id=\"9ad3659b9c0f8c5d141d2d06857874df\"\n" +
                "           date_modified=\"2012-10-23T17:15:21.966-04\"\n" +
                "           xmlns:n0=\"http://commcarehq.org/case/transaction/v2\">\n" +
                "    <n0:update>\n" +
                "      <n0:number>8</n0:number>\n" +
                "    </n0:update>\n" +
                "  </n0:case>\n" +
                "  <cc_delegation_stub delegation_id=\"0e6db0c4-d07f-435c-89e5-64855440605c\">\n" +
                "    <n1:case case_id=\"0e6db0c4-d07f-435c-89e5-64855440605c\"\n" +
                "             user_id=\"9ad3659b9c0f8c5d141d2d06857874df\"\n" +
                "             date_modified=\"2012-10-23T17:15:21.966-04\"\n" +
                "             xmlns:n1=\"http://commcarehq.org/case/transaction/v2\">\n" +
                "      <n1:close />\n" +
                "    </n1:case>\n" +
                "  </cc_delegation_stub>\n" +
                "  <n2:meta xmlns:n2=\"http://openrosa.org/jr/xforms\">\n" +
                "  <n2:deviceID>cloudcare</n2:deviceID>\n" +
                "  <n2:timeStart>2012-10-23T17:15:18.324-04</n2:timeStart>\n" +
                "  <n2:timeEnd>2012-10-23T17:15:21.966-04</n2:timeEnd>\n" +
                "  <n2:username>test</n2:username>\n" +
                "  <n2:userID>9ad3659b9c0f8c5d141d2d06857874df</n2:userID>\n" +
                "  <n2:instanceID>c24a85f9-703d-434c-b087-5759f3fa9937</n2:instanceID>\n" +
                "  <n3:appVersion xmlns:n3=\"http://commcarehq.org/xforms\">2.0</n3:appVersion>\n" +
                "  </n2:meta>\n" +
                "</data>";
    }
}
