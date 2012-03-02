package org.motechproject.appointments.api.model.jobs;

import org.junit.Test;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.scheduler.MotechSchedulerService;

import static junit.framework.Assert.assertEquals;

public class AppointmentReminderJobTest {

    private Visit visit;
    private AppointmentReminderJob appointmentReminderJob;

    public AppointmentReminderJobTest() {
        visit = new Visit().appointment(new Appointment().reminder(new Reminder())).name("visitName");
        appointmentReminderJob = new AppointmentReminderJob("externalId", visit);
    }

    @Test
    public void shouldCreateMotechEventWithTheCorrectSubject() {
        assertEquals("org.motechproject.appointments.api.Appointment.Reminder", appointmentReminderJob.getMotechEvent().getSubject());
    }

    @Test
    public void shouldRunEveryDay() {
        assertEquals("0 0 0 ? * *", appointmentReminderJob.getCronExpression());
    }

    @Test
    public void shouldAddVisitNameToParameters() {
        assertEquals(visit.name(), appointmentReminderJob.getMotechEvent().getParameters().get(EventKeys.VISIT_NAME));
    }

    @Test
    public void shouldAddCombinationOfVisitNameAndExternalId_AsJobId() {
        assertEquals("externalId" + visit.name(), appointmentReminderJob.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
    }
}