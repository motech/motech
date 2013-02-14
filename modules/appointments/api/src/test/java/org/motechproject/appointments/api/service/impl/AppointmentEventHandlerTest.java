package org.motechproject.appointments.api.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.appointments.api.service.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.service.contract.CreateVisitRequest;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentEventHandlerTest {

    @InjectMocks
    private AppointmentEventHandler appointmentEventHandler = new AppointmentEventHandler();

    @Mock
    private AppointmentService appointmentService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldCreateAppointmentFromEvent() {
        String externalId = "externalId";
        DateTime now = DateUtil.now();
        DateTime week1 = now.plusWeeks(1);
        DateTime week2 = now.plusWeeks(2);
        DateTime week4 = now.plusWeeks(4);

        Map<String, DateTime> visitRequests = new HashMap<>();
        visitRequests.put("baseline", week1);
        visitRequests.put("week2", week2);
        visitRequests.put("week4", week4);

        Map<String, Object> param = new HashMap<>();
        param.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        param.put(EventKeys.VISIT_REQUESTS, visitRequests);
        MotechEvent event = new MotechEvent(EventKeys.CREATE_APPOINTMENT_EVENT_SUBJECT, param);

        ArgumentCaptor<AppointmentCalendarRequest> appointmentCalendarRequestArgumentCaptor = ArgumentCaptor.forClass(AppointmentCalendarRequest.class);

        appointmentEventHandler.addCalendar(event);
        verify(appointmentService).addCalendar(appointmentCalendarRequestArgumentCaptor.capture());

        AppointmentCalendarRequest appointmentCalendarRequest = appointmentCalendarRequestArgumentCaptor.getValue();


        assertEquals(externalId, appointmentCalendarRequest.getExternalId());
        assertEquals("week4", appointmentCalendarRequest.getCreateVisitRequests().get(0).getVisitName());
        assertEquals(week4, appointmentCalendarRequest.getCreateVisitRequests().get(0).getAppointmentDueDate());
        assertEquals("week2", appointmentCalendarRequest.getCreateVisitRequests().get(1).getVisitName());
        assertEquals(week2, appointmentCalendarRequest.getCreateVisitRequests().get(1).getAppointmentDueDate());
        assertEquals("baseline", appointmentCalendarRequest.getCreateVisitRequests().get(2).getVisitName());
        assertEquals(week1, appointmentCalendarRequest.getCreateVisitRequests().get(2).getAppointmentDueDate());
    }

    @Test
    public void shouldCreateVisitFromEvent() {
        final String externalId = "externalId";
        final String visitName = "visit";
        final DateTime now = DateUtil.now();

        Map<String, Object> param = new HashMap<>();
        param.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        param.put(EventKeys.VISIT_NAME, visitName);
        param.put(EventKeys.VISIT_DATE, now);
        param.put(EventKeys.REMIND_FROM, 10);
        param.put(EventKeys.INTERVAL_COUNT, 1);
        param.put(EventKeys.INTERVAL_UNIT, "DAYS");
        param.put(EventKeys.REPEAT_COUNT, 20);

        MotechEvent event = new MotechEvent(EventKeys.CREATE_VISIT_EVENT_SUBJECT, param);

        ArgumentCaptor<CreateVisitRequest> createVisitRequestArgumentCaptor = ArgumentCaptor.forClass(CreateVisitRequest.class);

        appointmentEventHandler.createVisit(event);

        verify(appointmentService).addVisit(eq(externalId), createVisitRequestArgumentCaptor.capture());

        CreateVisitRequest visitRequest = createVisitRequestArgumentCaptor.getValue();

        assertEquals(visitName, visitRequest.getVisitName());
        assertEquals(now, visitRequest.getAppointmentDueDate());
        assertEquals(10, visitRequest.getAppointmentReminderConfigurations().get(0).getRemindFrom());
        assertEquals(1, visitRequest.getAppointmentReminderConfigurations().get(0).getIntervalCount());
        assertEquals(ReminderConfiguration.IntervalUnit.DAYS, visitRequest.getAppointmentReminderConfigurations().get(0).getIntervalUnit());
        assertEquals(20, visitRequest.getAppointmentReminderConfigurations().get(0).getRepeatCount());
    }
}
