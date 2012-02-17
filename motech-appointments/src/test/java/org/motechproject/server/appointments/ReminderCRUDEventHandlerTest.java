package org.motechproject.server.appointments;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.ReminderService;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ReminderCRUDEventHandlerTest {


    @Mock
    private ReminderService reminderService;

    @Mock
    private MotechSchedulerService schedulerService;

    @Mock
    private MetricsAgent metricsAgent;

    ReminderCRUDEventHandler reminderCRUDEventHandler;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        reminderCRUDEventHandler = new ReminderCRUDEventHandler(schedulerService, reminderService);
    }

    @Test
    public void testHandle_Delete() throws Exception {
        reminderCRUDEventHandler.delete("foo");
        verify(schedulerService, times(1)).safeUnscheduleJob(EventKeys.APPOINTMENT_REMINDER_EVENT_PREFIX, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandle_DeleteNoJobId() throws Exception {
        reminderCRUDEventHandler.delete(null);
        verify(schedulerService, times(0)).safeUnscheduleJob(EventKeys.APPOINTMENT_REMINDER_EVENT_PREFIX, "foo");
    }

    @Test
    public void testHandle_Created() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(EventKeys.REMINDER_ID_KEY, "foo");
        params.put(EventKeys.APPOINTMENT_ID_KEY, "bar");
        MotechEvent motechEvent = new MotechEvent("created", params);

        Reminder r = new Reminder();
        r.setEnabled(true);
        r.setStartDate(DateUtil.today().plusDays(2).toDate());
        r.setEndDate(DateUtil.today().plusDays(3).toDate());
        when(reminderService.getReminder("foo")).thenReturn(r);

        reminderCRUDEventHandler.create(motechEvent);
        verify(schedulerService).safeScheduleRunOnceJob(Matchers.<RunOnceSchedulableJob>any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandle_CreatedNoReminderId() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(EventKeys.REMINDER_ID_KEY, "");
        MotechEvent motechEvent = new MotechEvent("created", params);
        reminderCRUDEventHandler.create(motechEvent);
    }

    @Test
    public void testHandle_UpdateNoReminderId() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();

        MotechEvent motechEvent = new MotechEvent("updated", params);

        reminderCRUDEventHandler.update(motechEvent);

        verify(schedulerService, times(0)).safeScheduleRunOnceJob(any(RunOnceSchedulableJob.class));
        verify(schedulerService, times(0)).safeScheduleRepeatingJob(any(RepeatingSchedulableJob.class));
        verify(schedulerService, times(0)).safeUnscheduleJob(anyString(), anyString());
    }

    @Test
    public void testHandle_UpdateDisabled() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.REMINDER_ID_KEY, "foo");

        MotechEvent motechEvent = new MotechEvent("updated", params);

        Reminder r = new Reminder();
        r.setEnabled(false);
        r.setExternalId("externalId");
        when(reminderService.getReminder("foo")).thenReturn(r);

        reminderCRUDEventHandler.update(motechEvent);

        verify(schedulerService, times(1)).safeUnscheduleJob(EventKeys.APPOINTMENT_REMINDER_EVENT_PREFIX, r.getExternalId());
    }


    @Test
    public void testHandle_UpdateEnabledNoReminderId() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.REMINDER_ID_KEY, "foo");

        MotechEvent motechEvent = new MotechEvent("updated", params);

        Reminder r = new Reminder();
        r.setEnabled(true);
        when(reminderService.getReminder("foo")).thenReturn(r);

        reminderCRUDEventHandler.update(motechEvent);

        verify(schedulerService, times(0)).safeScheduleRunOnceJob(any(RunOnceSchedulableJob.class));
        verify(schedulerService, times(0)).safeScheduleRepeatingJob(any(RepeatingSchedulableJob.class));
    }

    @Test
    public void testHandle_UpdateEnabledNullAptId() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.REMINDER_ID_KEY, "foo");

        MotechEvent motechEvent = new MotechEvent("updated", params);

        Reminder r = new Reminder();
        r.setEnabled(true);
        r.setStartDate(new Date());

        when(reminderService.getReminder("foo")).thenReturn(r);

        reminderCRUDEventHandler.update(motechEvent);

        verify(schedulerService, times(0)).safeScheduleRunOnceJob(any(RunOnceSchedulableJob.class));
        verify(schedulerService, times(0)).safeScheduleRepeatingJob(any(RepeatingSchedulableJob.class));
    }

    @Test
    public void testHandle_CreateEnabledNoUnits() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.REMINDER_ID_KEY, "foo");
        params.put(EventKeys.APPOINTMENT_ID_KEY, "bar");

        MotechEvent motechEvent = new MotechEvent("updated", params);

        Reminder r = new Reminder();
        r.setEnabled(true);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        r.setStartDate(cal.getTime());

        when(reminderService.getReminder("foo")).thenReturn(r);

        reminderCRUDEventHandler.create(motechEvent);

        verify(schedulerService, times(1)).safeScheduleRunOnceJob(any(RunOnceSchedulableJob.class));
        verify(schedulerService, times(0)).safeScheduleRepeatingJob(any(RepeatingSchedulableJob.class));
    }

    @Test
    public void testHandle_CreateEnabledRepeating() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.REMINDER_ID_KEY, "foo");
        params.put(EventKeys.APPOINTMENT_ID_KEY, "bar");

        MotechEvent motechEvent = new MotechEvent("updated", params);

        Reminder r = new Reminder();
        r.setEnabled(true);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        r.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        r.setEndDate(cal.getTime());
        r.setIntervalCount(1);
        r.setUnits(Reminder.intervalUnits.HOURS);
        r.setRepeatCount(7);

        when(reminderService.getReminder("foo")).thenReturn(r);

        reminderCRUDEventHandler.create(motechEvent);

        verify(schedulerService, times(0)).safeScheduleRunOnceJob(any(RunOnceSchedulableJob.class));
        verify(schedulerService, times(1)).safeScheduleRepeatingJob(any(RepeatingSchedulableJob.class));
    }
}
