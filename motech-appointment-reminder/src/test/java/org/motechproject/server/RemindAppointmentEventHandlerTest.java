package org.motechproject.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.service.AppointmentReminderService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RemindAppointmentEventHandlerTest {


    @InjectMocks
    RemindAppointmentEventHandler remindAppointmentEventHandler = new RemindAppointmentEventHandler();

    @Mock
    private AppointmentReminderService appointmentReminderService;

    @Before
    public void initMocks() {

        MockitoAnnotations.initMocks(this);
     }


    @Test
    public void testHandle() throws Exception {

        String appointmentId = "1a";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RemindAppointmentEventHandler.APPOINTMENT_ID_KEY, appointmentId);

        MotechEvent motechEvent = new MotechEvent("", "", params);

       remindAppointmentEventHandler.handle(motechEvent);

        verify(appointmentReminderService, times(1)).remindPatientAppointment(appointmentId);

    }

    @Test
    public void testHandleInvalidAppointmentIdType() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RemindAppointmentEventHandler.APPOINTMENT_ID_KEY, new Integer(0));

        MotechEvent motechEvent = new MotechEvent("", "", params);

       remindAppointmentEventHandler.handle(motechEvent);

        verify(appointmentReminderService, times(0)).remindPatientAppointment(anyString());

    }

    @Test
    public void testHandleNoAppointmentId() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();

        MotechEvent motechEvent = new MotechEvent("", "", params);

       remindAppointmentEventHandler.handle(motechEvent);

        verify(appointmentReminderService, times(0)).remindPatientAppointment(anyString());

    }

    @Test
    public void testGetIdentifier() throws Exception {

    }
}
