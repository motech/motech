package org.motechproject.appointments.api.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.dao.AllReminderJobs;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentServiceTest {

    @Mock
    private AllReminderJobs allReminderJobs;
    @Mock
    private AllAppointmentCalendars allAppointmentCalendars;

    AppointmentService appointmentService;

    @Before
    public void setUp() {
        initMocks(this);
        appointmentService = new AppointmentService(allAppointmentCalendars, allReminderJobs);
    }

    @Test
    public void shouldAddVisitToAppointmentCalendar() {
        String externalId = "externalId";
        Reminder appointmentReminder = new Reminder();
        Visit visit = new Visit().appointment(new Appointment().reminder(appointmentReminder));

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(null);

        appointmentService.addVisit(visit, externalId);

        ArgumentCaptor<AppointmentCalendar> appointmentCalendarArgumentCaptor = ArgumentCaptor.forClass(AppointmentCalendar.class);
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendarArgumentCaptor.capture());
        assertEquals(externalId, appointmentCalendarArgumentCaptor.getValue().externalId());
        assertEquals(visit, appointmentCalendarArgumentCaptor.getValue().visits().get(0));
        verify(allReminderJobs).add(appointmentReminder, externalId);
    }

    @Test
    public void shouldAddVisitToExistingAppointmentCalendar() {
        String externalId = "externalId";
        Reminder appointmentReminder = new Reminder();
        Visit visit = new Visit().appointment(new Appointment().reminder(appointmentReminder));

        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(externalId);
        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.addVisit(visit, externalId);

        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendar);
        assertEquals(visit, appointmentCalendar.visits().get(0));
        verify(allReminderJobs).add(appointmentReminder, externalId);
    }
}
