package org.motechproject.mobileforms.api.callbacks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.TestFormBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mobileforms.api.callbacks.FormGroupPublisher.FORM_BEAN_GROUP;
import static org.motechproject.mobileforms.api.callbacks.FormGroupPublisher.FORM_VALID_FROMS;

public class FormGroupPublisherTest {

    private FormGroupPublisher publisher;

    @Mock
    private EventRelay eventRelay;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        publisher = new FormGroupPublisher();
        ReflectionTestUtils.setField(publisher, "eventRelay", eventRelay);
    }

    @Test
    public void shouldPublishFormGroup() {
        final FormBeanGroup formBeanGroup = mock(FormBeanGroup.class);
        publisher.publish(formBeanGroup);
        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(eventCaptor.capture());
        final MotechEvent actualEvent = eventCaptor.getValue();

        assertThat(actualEvent.getSubject(), is(equalTo(FORM_VALID_FROMS)));
        final Map<String, Object> expectedParameters = new HashMap<String, Object>() {{
            put(FORM_BEAN_GROUP, formBeanGroup);
        }};
        assertThat(actualEvent.getParameters(), is(equalTo(expectedParameters)));
    }

    @Test
    public void shouldAddErrorMessageToAllFormsInTheGroupOnEncounteringRuntimeException() {
        final TestFormBean formBeanOne = new TestFormBean(null, "form1", null, null, null, null, null, null);
        final TestFormBean formBeanTwo = new TestFormBean(null, "form2", null, null, null, null, null, null);
        FormBeanGroup formBeanGroup = new FormBeanGroup(Arrays.<FormBean>asList(formBeanOne, formBeanTwo));
        Map<String, Object> params = new HashMap<>();
        params.put(FORM_BEAN_GROUP, formBeanGroup);
        doThrow(new RuntimeException()).when(eventRelay).sendEventMessage(new MotechEvent(FORM_VALID_FROMS, params));
        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        publisher.publish(formBeanGroup);

        assertThat(formBeanOne.getFormErrors(), is(equalTo(Arrays.asList(new FormError("Form Error:form1", "Server exception, contact your administrator")))));
        assertThat(formBeanTwo.getFormErrors(), is(equalTo(Arrays.asList(new FormError("Form Error:form2", "Server exception, contact your administrator")))));
        verify(eventRelay, times(2)).sendEventMessage(eventCaptor.capture());
        final MotechEvent event = eventCaptor.getValue();
        assertThat(event.getSubject(), is(equalTo(FormGroupPublisher.FORM_ERROR)));
    }
}
