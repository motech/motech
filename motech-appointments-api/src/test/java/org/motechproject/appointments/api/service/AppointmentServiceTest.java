package org.motechproject.appointments.api.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.dao.AllAppointmentReminderJobs;
import org.motechproject.appointments.api.dao.AllVisitReminderJobs;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
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
        DateTime now = DateUtil.now();
        VisitRequest baseline = new VisitRequest().setDueDate(now.plusWeeks(1));
        VisitRequest week2Visit = new VisitRequest().setDueDate(now.plusWeeks(2)).setReminderConfiguration(new ReminderConfiguration());
        VisitRequest week4Visit = new VisitRequest().setDueDate(now.plusWeeks(4)).setReminderConfiguration(new ReminderConfiguration());
        AppointmentCalendarRequest appointmentCalendarRequest = new AppointmentCalendarRequest().setExternalId(externalId)
                .addVisitRequest("baseline", baseline).addVisitRequest("week2", week2Visit).addVisitRequest("week4", week4Visit);

        appointmentService.addCalendar(appointmentCalendarRequest);

        ArgumentCaptor<AppointmentCalendar> appointmentCalendarArgumentCaptor = ArgumentCaptor.forClass(AppointmentCalendar.class);
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendarArgumentCaptor.capture());

        AppointmentCalendar calendar = appointmentCalendarArgumentCaptor.getValue();
        assertEquals(externalId, calendar.externalId());
        assertEquals(3, calendar.visits().size());
        assertEquals("week4", calendar.visits().get(0).name());
        assertEquals("week2", calendar.visits().get(1).name());
        assertEquals("baseline", calendar.visits().get(2).name());

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

    @Test
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
        ReminderConfiguration reminderConfiguration = new ReminderConfiguration().setRemindFrom(REMIND_FROM).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);
        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        VisitRequest visitRequest = new VisitRequest().setDueDate(now).setReminderConfiguration(reminderConfiguration);
        String visitName = appointmentService.addVisit(externalId, "visitName", visitRequest);

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(allAppointmentReminderJobs).add(appointmentCaptor.capture(), eq(externalId));
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendar);

        assertEquals(visitName, visitName);
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
    public void shouldFindVisitByExternalId_AndName(){
        String externalId = "externalId";
        String visitName = "visit";

        Visit visit = new Visit().name(visitName).appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        assertEquals(visit, appointmentService.findVisit(externalId, visitName));
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
