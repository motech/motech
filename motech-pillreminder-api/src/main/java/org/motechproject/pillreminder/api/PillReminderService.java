package org.motechproject.pillreminder.api;

import java.util.HashMap;
import java.util.Map;

import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.pillreminder.api.dao.PillReminderDao;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.springframework.beans.factory.annotation.Autowired;

public class PillReminderService {

	@Autowired(required = false)
	private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

	@Autowired
	private PillReminderDao pillReminderDao;

	public void addPillReminder(PillReminder pillReminder) {
		pillReminderDao.add(pillReminder);
		eventRelay.sendEventMessage(getSkinnyEvent(pillReminder, EventKeys.PILLREMINDER_CREATED_SUBJECT));
	}

	public void updatePillReminder(PillReminder pillReminder) {
		pillReminderDao.update(pillReminder);
		eventRelay.sendEventMessage(getSkinnyEvent(pillReminder, EventKeys.PILLREMINDER_UPDATED_SUBJECT));
	}

	public void removePillReminder(PillReminder pillReminder) {
		pillReminderDao.remove(pillReminder);
		eventRelay.sendEventMessage(getSkinnyEvent(pillReminder, EventKeys.PILLREMINDER_DELETED_SUBJECT));
	}

	public PillReminder getPillReminder(String pillReminderId) {
		PillReminder appointment = pillReminderDao.get(pillReminderId);
		return appointment;
	}

	private MotechEvent getSkinnyEvent(PillReminder pillReminder, String subject) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(EventKeys.PILLREMINDER_ID_KEY, pillReminder.getId());
		MotechEvent event = new MotechEvent(subject, parameters);
		return event;
	}

}
