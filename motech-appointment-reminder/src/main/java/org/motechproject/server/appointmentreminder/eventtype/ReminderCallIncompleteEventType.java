package org.motechproject.server.appointmentreminder.eventtype;

import org.motechproject.event.EventType;

/**

 */
public class ReminderCallIncompleteEventType implements EventType
{

	public static final String KEY = "appointment-reminder.call.incomplete";

	private static ReminderCallIncompleteEventType instance = new ReminderCallIncompleteEventType();

	private ReminderCallIncompleteEventType() {
	}

	public static EventType getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "AppointmentReminderCallIncomplete";
	}

	@Override
	public String getKey() {
		return KEY;
	}

}
