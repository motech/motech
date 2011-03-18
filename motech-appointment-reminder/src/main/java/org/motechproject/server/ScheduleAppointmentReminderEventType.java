package org.motechproject.server;

import org.motechproject.event.EventType;

/**
 * Defines an @see EventType of event that a @see ScheduleAppointmentReminderHandler will be listening for.
 * @author yyonkov
 *
 */
public class ScheduleAppointmentReminderEventType implements EventType {

	@Override
	public String getName() {
		return "ScheduleAppointmentReminderEventType";
	}

	@Override
	public String getKey() {
		return "scheduleappointmentremindereventtype";
	}

}
