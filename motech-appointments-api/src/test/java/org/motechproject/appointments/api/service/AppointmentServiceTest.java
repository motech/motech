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
import org.motechproject.appointments.api.dao.AllAppointmentReminderJobs;
import org.motechproject.appointments.api.dao.AllVisitReminderJobs;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentServiceTest {

    public static final int REMIND_FROM = 10;
    @Mock
    private AllAppointmentReminderJobs allAppointmentReminderJobs;
    @Mock
    private AllAppointmentCalendars allAppointmentCalendars;
    @Mock
    private AllVisitReminderJobs allVisitReminderJobs;

    AppointmentService appointmentService;

    @Before
    public void setUp() {
        initMocks(this);
        appointmentService = new AppointmentService(allAppointmentCalendars, allAppointmentReminderJobs, allVisitReminderJobs);
    }

    @Test
    public void shouldAddAnAppointmentCalender() {
        String externalId = "externalId";
        AppointmentCalendarRequest appointmentCalendarRequest = new AppointmentCalendarRequest().setExternalId(externalId).setWeekOffsets(Arrays.asList(2, 4)).setAppointmentReminderConfiguration(new ReminderConfiguration());

        appointmentService.addCalendar(appointmentCalendarRequest);

        ArgumentCaptor<AppointmentCalendar> appointmentCalendarArgumentCaptor = ArgumentCaptor.forClass(AppointmentCalendar.class);
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendarArgumentCaptor.capture());

        AppointmentCalendar calendar = appointmentCalendarArgumentCaptor.getValue();
        assertEquals(externalId, calendar.externalId());
        assertEquals(3, calendar.visits().size());
        assertEquals("baseline", calendar.visits().get(0).name());
        assertEquals("week2", calendar.visits().get(1).name());
        assertEquals("week4", calendar.visits().get(2).name());

        verify(allAppointmentReminderJobs, times(2)).add(Matchers.<Appointment>any(), eq(externalId));
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

    public void shouldRemoveAnAppointmentCalender() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();
        when(allAppointmentCalendars.findByExternalId("externalId")).thenReturn(appointmentCalendar);
        appointmentService.removeCalendar("externalId");

        verify(allAppointmentCalendars).remove(appointmentCalendar);
    }

    @Test
    public void shouldUnscheduleAllReminders() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId("someExternalId");
        when(allAppointmentCalendars.findByExternalId("externalId")).thenReturn(appointmentCalendar);
        appointmentService.removeCalendar("externalId");

        verify(allAppointmentReminderJobs).remove("someExternalId");
    }

    @Test
    public void shouldAddAVisit() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();
        final String externalId = "externalId";
        final DateTime now = DateUtil.now();
        ReminderConfiguration appointmentReminderConfiguration = new ReminderConfiguration().setRemindFrom(REMIND_FROM).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        final String visitId = appointmentService.addVisit(externalId, now, appointmentReminderConfiguration, TypeOfVisit.Scheduled);

        assertNotNull(visitId);
        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);

        verify(allAppointmentReminderJobs).add(appointmentCaptor.capture(), eq(externalId));
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendar);

        assertEquals(now, appointmentCaptor.getValue().dueDate());
        assertEquals(now.toLocalDate().minusDays(REMIND_FROM).toDate(), appointmentCaptor.getValue().reminder().startDate());
    }

    @Test
    public void shouldUpdateVisit() {
        String externalId = "externalId";
        String visitName = "visit";
        DateTime now = DateUtil.now();

        Visit visit = new Visit().name(visitName).appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);
        visit.appointment().confirmedDate(now.minusDays(10));

        ReminderConfiguration visitReminderConfiguration = new ReminderConfiguration().setRemindFrom(REMIND_FROM).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);
        ArgumentCaptor<AppointmentCalendar> appointmentCalendarArgumentCaptor = ArgumentCaptor.forClass(AppointmentCalendar.class);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.confirmVisit(externalId, visitName, now, visitReminderConfiguration);

        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendarArgumentCaptor.capture());
        assertEquals(externalId, appointmentCalendarArgumentCaptor.getValue().externalId());
        assertEquals(visit, appointmentCalendarArgumentCaptor.getValue().getVisit(visitName));
    }

    @Test
    public void shouldUnScheduleVisitReminderJobsWhenVisitDateIsSet() {
        String externalId = "externalId";
        String visitId = "visitId";
        DateTime visitDate = DateUtil.now();

        Visit visit = new Visit().name(visitId);
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(externalId).addVisit(visit);
        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.setVisitDate(externalId, visitId, visitDate);
        verify(allVisitReminderJobs).remove(externalId);
    }

    @Test
    public void shouldUpdateVisitWhenVisitDateIsSet() {
        DateTime visitDate = DateUtil.now();
        String visitName = "visitName";
        final String externalId = "externalId";

        Visit visit = new Visit().name(visitName);
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(externalId).addVisit(visit);
        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.setVisitDate(externalId, visitName, visitDate);

        ArgumentCaptor<AppointmentCalendar> appointmentCalendarCaptor = ArgumentCaptor.forClass(AppointmentCalendar.class);
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendarCaptor.capture());
        assertEquals(visitDate, appointmentCalendarCaptor.getValue().getVisit(visitName).visitDate());
    }

    @Test
    public void shouldUnScheduleOldVisitReminderJob() {
        String externalId = "externalId";
        String visitName = "visit";
        DateTime now = DateUtil.now();

        Visit visit = new Visit().name(visitName).appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);
        visit.appointment().confirmedDate(now.minusDays(10));

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.confirmVisit(externalId, visitName, now, new ReminderConfiguration());

        verify(allVisitReminderJobs).remove(externalId);
    }

    @Test
    public void shouldScheduleVisitReminderJob() {
        String externalId = "externalId";
        String visitName = "visit";
        DateTime now = DateUtil.now();

        Visit visit = new Visit().name(visitName).appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.confirmVisit(externalId, visitName, now, new ReminderConfiguration());

        verify(allVisitReminderJobs).add(visit, externalId);
    }

    @Test
    public void shouldReturnAppointmentGivenId() {
        Appointment appointment = new Appointment();
        when(allAppointmentCalendars.findAppointmentById(appointment.id())).thenReturn(appointment);
        assertEquals(appointment, appointmentService.getAppointment(appointment.id()));
    }
}
