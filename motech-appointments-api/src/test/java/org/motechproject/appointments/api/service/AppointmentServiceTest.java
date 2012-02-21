package org.motechproject.appointments.api.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.dao.AllReminderJobs;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
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
    public void shouldAddAnAppointmentCalender() {
        String externalId = "externalId";
        AppointmentCalendarRequest appointmentCalendarRequest = new AppointmentCalendarRequest().setExternalId(externalId).setWeekOffsets(Arrays.asList(2, 4)).setReminderConfiguration(new ReminderConfiguration());

        appointmentService.addCalendar(appointmentCalendarRequest);

        ArgumentCaptor<AppointmentCalendar> appointmentCalendarArgumentCaptor = ArgumentCaptor.forClass(AppointmentCalendar.class);
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendarArgumentCaptor.capture());

        AppointmentCalendar calendar = appointmentCalendarArgumentCaptor.getValue();
        assertEquals(externalId, calendar.externalId());
        assertEquals(3, calendar.visits().size());
        assertEquals("baseline", calendar.visits().get(0).name());
        assertEquals("week2", calendar.visits().get(1).name());
        assertEquals("week4", calendar.visits().get(2).name());

        verify(allReminderJobs, times(2)).add(Matchers.<Reminder>any(), eq(externalId));
    }

    @Test
    public void shouldUpdateVisit_OnAnAppointmentCalendar() {
        String externalId = "externalId";
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();
        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        DateTime updatedVisitDate = DateUtil.now().minusDays(2);
        Visit updatedVisit = new Visit().name("baseline").visitDate(updatedVisitDate);
        appointmentService.updateVisit(updatedVisit, externalId);

        ArgumentCaptor<AppointmentCalendar> appointmentCalendarArgumentCaptor = ArgumentCaptor.forClass(AppointmentCalendar.class);
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendarArgumentCaptor.capture());
        assertEquals(updatedVisit, appointmentCalendarArgumentCaptor.getValue().getVisit("baseline"));
        assertEquals(updatedVisitDate, appointmentCalendarArgumentCaptor.getValue().getVisit("baseline").visitDate());
    }

    @Test
    public void shouldRemoveAnAppointmentCalender(){
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();
        when(allAppointmentCalendars.findByExternalId("externalId")).thenReturn(appointmentCalendar);
        appointmentService.removeCalendar("externalId");

        verify(allAppointmentCalendars).remove(appointmentCalendar);
    }

    @Test
    public void shouldUnscheduleAllReminders(){
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId("someExternalId");
        when(allAppointmentCalendars.findByExternalId("externalId")).thenReturn(appointmentCalendar);
        appointmentService.removeCalendar("externalId");

        verify(allReminderJobs).remove("someExternalId");
    }
}
