package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;
import org.motechproject.appointments.api.service.contract.RescheduleAppointmentRequest;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.util.DateUtil;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class RescheduleAppointmentMapperTest {

    private DateTime appointmentDueDate;
    private RescheduleAppointmentRequest rescheduleAppointmentRequest;

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
    public void shouldReturnAListOfReminders_ForRescheduledAppointment(){

        List<Reminder> reminders = new RescheduleAppointmentMapper().map(rescheduleAppointmentRequest);

        assertEquals(1, reminders.size());
        assertEquals(DateUtil.newDate(appointmentDueDate.minusDays(10)), DateUtil.newDate(reminders.get(0).startDate()));
        assertEquals(DateUtil.newDate(appointmentDueDate), DateUtil.newDate(reminders.get(0).endDate()));
    }
}
