package org.motechproject.openmrs.omod.advice;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientAdviceTest {

    private PatientAdvice patientAdvice;
    @Mock
    private EventRelay eventRelay;

    @Before
    public void setUp() {
        initMocks(this);
        patientAdvice = new PatientAdvice();

        ReflectionTestUtils.setField(patientAdvice, "eventRelay", eventRelay);
    }

    @Test
    public void shouldPublishEventForPatientRelatedOperations() throws Throwable {
        Method method1 = PatientService.class.getDeclaredMethods()[0];
        String methodName = method1.getName();
        Patient patient = mock(Patient.class);

        patientAdvice.afterReturning(Arrays.asList(patient), method1, null, null);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captor.capture());
        MotechEvent captured = captor.getValue();
        assertThat((String) captured.getParameters().get("method"), is(equalTo(methodName)));
    }
}
