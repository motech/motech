package org.motechproject.appointments.api;

import org.motechproject.appointments.api.dao.AllReminders;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReminderService {
    @Autowired(required = false)
    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

    @Autowired
    private AllReminders allReminders;

    public void addReminder(Reminder reminder) {
        if (null == reminder.getAppointmentId()) {
            throw new IllegalArgumentException("Reminder must be associated with an appointment");
        }

        allReminders.add(reminder);

        eventRelay.sendEventMessage(getSkinnyEvent(reminder, EventKeys.REMINDER_CREATED_SUBJECT));
    }

    public void updateReminder(Reminder reminder) {
        if (null == reminder.getAppointmentId()) {
            throw new IllegalArgumentException("Reminder must be associated with an appointment");
        }

        allReminders.update(reminder);

        eventRelay.sendEventMessage(getSkinnyEvent(reminder, EventKeys.REMINDER_UPDATED_SUBJECT));
    }

    public void removeReminder(String reminderId) {
        Reminder reminder = getReminder(reminderId);

        removeReminder(reminder);
    }

    public void removeReminder(Reminder reminder) {
        MotechEvent event = getSkinnyEvent(reminder, EventKeys.REMINDER_DELETED_SUBJECT);

        allReminders.remove(reminder);

        eventRelay.sendEventMessage(event);
    }

    public Reminder getReminder(String reminderId) {
        return allReminders.get(reminderId);
    }

    public List<Reminder> findByAppointmentId(String appointmentId) {
        return allReminders.findByAppointmentId(appointmentId);
    }

    public List<Reminder> findByExternalId(String externalId) {
        return allReminders.findByExternalId(externalId);
    }

    private MotechEvent getSkinnyEvent(Reminder reminder, String subject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.APPOINTMENT_ID_KEY, reminder.getAppointmentId());
        parameters.put(EventKeys.REMINDER_ID_KEY, reminder.getId());
        parameters.put(EventKeys.EXTERNAL_ID_KEY, reminder.getExternalId());
        return new MotechEvent(subject, parameters);
    }
}
