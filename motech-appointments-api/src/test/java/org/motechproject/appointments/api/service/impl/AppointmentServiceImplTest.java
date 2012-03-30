package org.motechproject.appointments.api.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.service.contract.*;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;
import org.motechproject.appointments.api.repository.AllReminderJobs;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentServiceImplTest {

    public static final int REMIND_FROM = 10;
    @Mock
    private AllAppointmentCalendars allAppointmentCalendars;
    @Mock
    private AllReminderJobs allReminderJobs;
    @Mock
    private VisitsQueryService visitsQueryService;

    AppointmentService appointmentService;

    @Before
    public void setUp() {
        initMocks(this);
        appointmentService = new AppointmentServiceImpl(allAppointmentCalendars, allReminderJobs, visitsQueryService);
    }

    @Test
    public void shouldAddAnAppointmentCalender_AndCreateAppointmentReminderJobsForTheAppointments() {
        String externalId = "externalId";
        DateTime now = DateUtil.now();
        CreateVisitRequest baseline = new CreateVisitRequest().setVisitName("baseline").setAppointmentDueDate(now.plusWeeks(1));
        CreateVisitRequest week2CreateVisit = new CreateVisitRequest().setVisitName("week2").setAppointmentDueDate(now.plusWeeks(2))
                .addAppointmentReminderConfiguration(new ReminderConfiguration());
        CreateVisitRequest week4CreateVisit = new CreateVisitRequest().setVisitName("week4").setAppointmentDueDate(now.plusWeeks(4))
                .addAppointmentReminderConfiguration(new ReminderConfiguration());
        AppointmentCalendarRequest appointmentCalendarRequest = new AppointmentCalendarRequest().setExternalId(externalId)
                .addVisitRequest(baseline)
                .addVisitRequest(week2CreateVisit)
                .addVisitRequest(week4CreateVisit);

        ArgumentCaptor<AppointmentCalendar> appointmentCalendarArgumentCaptor = ArgumentCaptor.forClass(AppointmentCalendar.class);

        appointmentService.addCalendar(appointmentCalendarRequest);

        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendarArgumentCaptor.capture());

        AppointmentCalendar calendar = appointmentCalendarArgumentCaptor.getValue();
        assertEquals(externalId, calendar.getExternalId());
        assertEquals(3, calendar.visits().size());
        assertEquals("baseline", calendar.visits().get(0).name());
        assertEquals("week2", calendar.visits().get(1).name());
        assertEquals("week4", calendar.visits().get(2).name());

        verify(allReminderJobs, times(3)).addAppointmentJob(eq(externalId), Matchers.<Visit>any());
    }

    @Test
    public void shouldRemoveAnAppointmentCalender() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();
        when(allAppointmentCalendars.findByExternalId("externalId")).thenReturn(appointmentCalendar);

        appointmentService.removeCalendar("externalId");

        verify(allAppointmentCalendars).remove(appointmentCalendar);
    }

    @Test
    public void removeCalendarShouldUnscheduleAllReminders() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId("externalId");
        when(allAppointmentCalendars.findByExternalId("externalId")).thenReturn(appointmentCalendar);

        appointmentService.removeCalendar("externalId");

        verify(allReminderJobs).removeAll("externalId");
    }

    @Test
    public void shouldAddAVisit() {
        final String externalId = "externalId";
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(externalId);
        final DateTime now = DateUtil.now();
        ReminderConfiguration reminderConfiguration = new ReminderConfiguration().setRemindFrom(REMIND_FROM).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);
        CreateVisitRequest createVisitRequest = new CreateVisitRequest().setAppointmentDueDate(now).addAppointmentReminderConfiguration(reminderConfiguration);
        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        String visitName = appointmentService.addVisit(externalId, createVisitRequest).getName();

        ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(allReminderJobs).addAppointmentJob(eq(externalId), visitCaptor.capture());
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendar);

        assertEquals(visitName, visitName);
        assertEquals(now, visitCaptor.getValue().appointment().dueDate());
        assertEquals(now.toLocalDate().minusDays(REMIND_FROM).toDate(), visitCaptor.getValue().appointment().reminders().get(0).startDate());
    }

    @Test
    public void shouldFindVisitByExternalId_AndName() {
        String externalId = "externalId";
        String visitName = "visit";

        Visit visit = new Visit().name(visitName).appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        assertEquals(visitName, appointmentService.findVisit(externalId, visitName).getName());
    }

    @Test
    public void confirmAppointmentShould_SetAppointmentConfirmDate_CreateVisitReminder_RemoveAppointmentJobs_RescheduleVisitJobs() {
        String externalId = "externalId";
        String visitName = "visit";
        DateTime now = DateUtil.now();

        Visit visit = new Visit().name(visitName).appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);
        ReminderConfiguration visitReminderConfiguration = new ReminderConfiguration().setRemindFrom(3);
        ConfirmAppointmentRequest confirmAppointmentRequest = new ConfirmAppointmentRequest().setAppointmentConfirmDate(now)
                .setExternalId(externalId)
                .setVisitName(visitName)
                .setVisitReminderConfiguration(visitReminderConfiguration);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.confirmAppointment(confirmAppointmentRequest);

        assertEquals(now, visit.appointment().confirmedDate());
        assertNotNull(visit.reminder());
        verify(allReminderJobs).removeAppointmentJob(externalId, visit);
        verify(allReminderJobs).rescheduleVisitJob(externalId, visit);
    }

    @Test
    public void shouldGetAllVisitsForGivenExternalId() {
        String externalId = "externalId";
        String visit1Name = "visit1";
        String visit2Name = "visit2";


        Visit visit1 = new Visit().name(visit1Name).appointment(new Appointment());
        Visit visit2 = new Visit().name(visit2Name).appointment(new Appointment());

        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit1).addVisit(visit2).externalId(externalId);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        List<VisitResponse> allVisits = appointmentService.getAllVisits(externalId);

        assertEquals(2, allVisits.size());
        assertEquals(visit1Name, allVisits.get(0).getName());
        assertEquals(visit2Name, allVisits.get(1).getName());
    }

    @Test
    public void rescheduleAppointment_AdjustsAppointmentDueDate_ReschedulesAppointmentReminderJobs() {
        String externalId = "externalId";
        String visitName = "visit";
        final DateTime now = DateUtil.now();
        final DateTime adjustedDueDate = DateUtil.now().plusDays(2);
        Visit visit = new Visit().name(visitName).appointment(new Appointment().dueDate(now));

        RescheduleAppointmentRequest rescheduleAppointmentRequest = new RescheduleAppointmentRequest().setExternalId(externalId).setVisitName(visitName).
                setAppointmentDueDate(adjustedDueDate).addAppointmentReminderConfiguration(new ReminderConfiguration());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.rescheduleAppointment(rescheduleAppointmentRequest);

        assertEquals(adjustedDueDate, visit.appointment().dueDate());
        assertEquals(1, visit.appointment().reminders().size());

        verify(allReminderJobs).rescheduleAppointmentJob(externalId, visit);
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendar);
    }

    @Test
    public void visiting_setsVisitDate_RemovesVisitReminderJob() {
        String externalId = "externalId";
        String visitName = "visit";
        final DateTime visitedDate = DateUtil.now();
        Visit visit = new Visit().name(visitName).appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.visited(externalId, visitName, visitedDate);

        assertEquals(visitedDate, visit.visitDate());
        verify(allReminderJobs).removeAll(externalId);
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendar);
    }

    @Test
    public void markingVisitAsMissed_MarksVisitAsMissed_RemovesAllAppointmentAndReminderJobs() {
        String externalId = "externalId";
        String visitName = "visit";
        Visit visit = new Visit().name(visitName).appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.markVisitAsMissed(externalId, visitName);

        assertTrue(visit.missed());
        verify(allReminderJobs).removeAll(externalId);
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendar);
    }

    @Test
    public void addingCustomDataToVisit_SetsDataOnDataMap(){
        String externalId = "externalId";
        String visitName = "visit";
        Visit visit = new Visit().name(visitName).appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit).externalId(externalId);
        HashMap<String, Object> dataMap = new HashMap<String, Object>() {{
            put("key", "value");
        }};

        when(allAppointmentCalendars.findByExternalId(externalId)).thenReturn(appointmentCalendar);

        appointmentService.addCustomDataToVisit(externalId, visitName, dataMap);

        assertEquals(dataMap, visit.getData());
        verify(allAppointmentCalendars).saveAppointmentCalendar(appointmentCalendar);
    }

    @Test
    public void shouldFindVisitsBasedOnQuery() {
        VisitsQuery query = mock(VisitsQuery.class);

        List<VisitResponse> visitResponses = mock(List.class);
        when(visitsQueryService.search(query)).thenReturn(visitResponses);

        assertEquals(visitResponses, appointmentService.search(query));
    }
}
