package org.motechproject.appointments.api.service.impl;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.appointments.api.service.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.service.contract.CreateVisitRequest;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AppointmentEventHandler {

    @Autowired
    private AppointmentService appointmentService;

    @MotechListener(subjects = { EventKeys.CREATE_APPOINTMENT_EVENT_SUBJECT })
    public void addCalendar(MotechEvent event) {

        String externalId = event.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        Map<String, DateTime> visitRequestMap = (HashMap) event.getParameters().get(EventKeys.VISIT_REQUESTS);

        AppointmentCalendarRequest calendar = new AppointmentCalendarRequest().setExternalId(externalId);
        for (Map.Entry<String, DateTime> entry : visitRequestMap.entrySet()) {
            CreateVisitRequest request = new CreateVisitRequest().setVisitName(entry.getKey())
                    .setAppointmentDueDate(entry.getValue())
                    .addAppointmentReminderConfiguration(new ReminderConfiguration());
            calendar.addVisitRequest(request);
        }

        appointmentService.addCalendar(calendar);
    }

    @MotechListener(subjects = { EventKeys.CREATE_VISIT_EVENT_SUBJECT })
    public void createVisit(MotechEvent event) {
        String externalId = event.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        String visitName = event.getParameters().get(EventKeys.VISIT_NAME).toString();
        DateTime visitDate = (DateTime)event.getParameters().get(EventKeys.VISIT_DATE);
        int remindFrom = (int)event.getParameters().get(EventKeys.REMIND_FROM);
        int intervalCount = (int)event.getParameters().get(EventKeys.INTERVAL_COUNT);
        ReminderConfiguration.IntervalUnit intervalUnit = ReminderConfiguration.IntervalUnit.valueOf(event.getParameters().get(EventKeys.INTERVAL_UNIT).toString());
        int repeatCount = (int)event.getParameters().get(EventKeys.REPEAT_COUNT);

        ReminderConfiguration reminderConfiguration = new ReminderConfiguration().setRemindFrom(remindFrom)
                .setIntervalCount(intervalCount)
                .setIntervalUnit(intervalUnit)
                .setRepeatCount(repeatCount);

        CreateVisitRequest visitRequest = new CreateVisitRequest().setVisitName(visitName).setAppointmentDueDate(visitDate).addAppointmentReminderConfiguration(reminderConfiguration);
        appointmentService.addVisit(externalId, visitRequest);
    }
}
