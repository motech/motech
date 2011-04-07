package org.motechproject.server.appointmentreminder.eventtype;

import org.motechproject.event.EventType;

/**

 */
public class ReminderCallCompleteEventType implements EventType
{

	public static final String KEY = "appointment-reminder.call.complete";

	private static ReminderCallCompleteEventType instance = new ReminderCallCompleteEventType();

	private ReminderCallCompleteEventType() {
	}

	public static EventType getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "AppointmentReminderCallComplete";
	}

	@Override
	public String getKey() {
		return KEY;
	}

}
