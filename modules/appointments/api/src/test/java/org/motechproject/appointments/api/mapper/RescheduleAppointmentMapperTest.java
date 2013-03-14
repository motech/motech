package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;
import org.motechproject.appointments.api.service.contract.RescheduleAppointmentRequest;
import org.motechproject.commons.date.util.DateUtil;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

public class RescheduleAppointmentMapperTest {

    private DateTime appointmentDueDate;
    private RescheduleAppointmentRequest rescheduleAppointmentRequest;

    @BeforeClass
    public static void startTimeFaking() {
        fakeNow(new DateTime(2020, 7, 15, 10, 0, 0));
    }

    @AfterClass
    public static void stopTimeFaking() {
        stopFakingTime();
    }

    @Before
    public void setUp() {
        appointmentDueDate = DateUtil.now();
        ReminderConfiguration appointmentReminderConfiguration = new ReminderConfiguration().setRemindFrom(10).setRepeatCount(10).setIntervalUnit(ReminderConfiguration.IntervalUnit.DAYS).setIntervalCount(1);
        rescheduleAppointmentRequest = new RescheduleAppointmentRequest().
                setExternalId("externalId").
                setVisitName("visit").
                setAppointmentDueDate(appointmentDueDate).
                addAppointmentReminderConfiguration(appointmentReminderConfiguration);

    }

    @Test
    public void shouldReturnAListOfReminders_ForRescheduledAppointment() {
        List<Reminder> reminders = new RescheduleAppointmentMapper().map(rescheduleAppointmentRequest);

        assertEquals(1, reminders.size());
        LocalDate expected = DateUtil.newDate(appointmentDueDate.minusDays(10));
        assertEquals(expected, DateUtil.newDate(reminders.get(0).startDate()));
        assertEquals(DateUtil.newDate(appointmentDueDate), DateUtil.newDate(reminders.get(0).endDate()));
    }


}
