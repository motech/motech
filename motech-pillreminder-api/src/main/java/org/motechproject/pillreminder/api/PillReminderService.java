package org.motechproject.pillreminder.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.pillreminder.api.dao.PillReminderDao;
import org.motechproject.pillreminder.api.model.Medicine;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.motechproject.pillreminder.api.model.Schedule;
import org.motechproject.pillreminder.api.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class PillReminderService {
	
    final Logger log = LoggerFactory.getLogger(PillReminderService.class);	

	@Autowired(required = false)
	private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

	@Autowired
	private PillReminderDao pillReminderDao;

	public void addPillReminder(PillReminder pillReminder) {
        log.info("Add PillReminder: " + pillReminder);
        Assert.notNull(pillReminder, "PillReminder can not be null.");
		pillReminderDao.add(pillReminder);
		eventRelay.sendEventMessage(getSkinnyEvent(pillReminder, EventKeys.PILLREMINDER_CREATED_SUBJECT));
	}

	public void updatePillReminder(PillReminder pillReminder) {
        log.info("Update PillReminder: " + pillReminder);
        Assert.notNull(pillReminder, "PillReminder can not be null.");
		pillReminderDao.update(pillReminder);
		eventRelay.sendEventMessage(getSkinnyEvent(pillReminder, EventKeys.PILLREMINDER_UPDATED_SUBJECT));
	}
	
	public void removePillReminder(String pillReminderId){
        log.info("Remove PillReminder: " + pillReminderId);	
        Assert.notNull(pillReminderId, "pillReminderId can not be null.");
		PillReminder pillReminder = getPillReminder(pillReminderId);
		if (pillReminder != null) {
			removePillReminder(pillReminder);
		}
	}

	public void removePillReminder(PillReminder pillReminder) {
        log.info("Remove PillReminder: " + pillReminder);
        Assert.notNull(pillReminder, "PillReminder can not be null.");			
//		pillReminderDao.remove(pillReminder); // called in handler
		eventRelay.sendEventMessage(getSkinnyEvent(pillReminder, EventKeys.PILLREMINDER_DELETED_SUBJECT));
	}
    
	public PillReminder getPillReminder(String pillReminderId) {
        Assert.notNull(pillReminderId, "pillReminderId can not be null.");
		PillReminder pillReminder = pillReminderDao.get(pillReminderId);
		return pillReminder;
	}
	
	public List<PillReminder> findByExternalId(String externalId) {
        Assert.notNull(externalId, "externalId can not be null.");
		return pillReminderDao.findByExternalId(externalId);
	}

	public List<PillReminder> getRemindersWithinWindow(String externalId, Date time) {
        Assert.notNull(externalId, "externalId can not be null.");
        Assert.notNull(time, "time can not be null.");
		return pillReminderDao.findByExternalIdAndWithinWindow(externalId, time);
	}
	
	/**
	 * 
	 * @param pillReminderId
	 * @param time
	 * @return
	 */
	public boolean isPillReminderCompleted(PillReminder pillReminder, Date time){
        Assert.notNull(pillReminder, "pillReminder can not be null.");
        Assert.notNull(time, "time can not be null.");
		List<String> medicineNames = getMedicinesWithinWindow(pillReminder, time);
		return (medicineNames.size() == 0);
	}
	
	/**
	 * Return a list of medicine names which haven't been taken within window
	 * 
	 * @param externalId
	 * @param time
	 * @return
	 */
	public List<String> getMedicinesWithinWindow(PillReminder pillReminder, Date time){
        Assert.notNull(pillReminder, "pillReminder can not be null.");
        Assert.notNull(time, "time can not be null.");
		
		List<String> medicineNames = new ArrayList<String>();
		Schedule schedule = pillReminder.getScheduleWithinWindow(time);
		if (schedule != null) {
			List<Medicine> medicines = pillReminder.getMedicines();
			for (Medicine medicine : medicines) {
				List<Status> statuses = medicine.getStatuses();
				if (statuses.isEmpty()) {
					medicineNames.add(medicine.getName());
				} else {
					for (Status status : statuses) {
						Date windowStartTime = schedule.getWindowStart().getTimeOfDate(time);
						if (status.getWindowStartTimeWithDate().equals(windowStartTime)
								&& !status.getTaken()) {
							medicineNames.add(medicine.getName());
						}
					}
				}
			}
		}
		return medicineNames;
	}
	
	/**
	 * 
	 * @param externalId
	 * @param medicineName
	 * @param windowStartTime
	 * @return
	 */
	public boolean getResult(String externalId, String medicineName, Date windowStartTime){
        Assert.notNull(externalId, "externalId can not be null.");
        Assert.notNull(medicineName, "medicineName can not be null.");
        Assert.notNull(windowStartTime, "windowStartTime can not be null.");
		boolean result = false;
		List<PillReminder> pillReminders = getRemindersWithinWindow(externalId, windowStartTime);
		for (PillReminder pillReminder : pillReminders) {
			List<Medicine> medicines = pillReminder.getMedicines();
			for (Medicine medicine : medicines) {
				if (medicine.getName().equals(medicineName)) {
					List<Status> statuses = medicine.getStatuses();
					for (Status status : statuses) {
						if (status.getWindowStartTimeWithDate().equals(windowStartTime)) {
							result = status.getTaken();
						}
					}
				}
			}
		}
		return result;
	}

	private MotechEvent getSkinnyEvent(PillReminder pillReminder, String subject) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(EventKeys.PILLREMINDER_ID_KEY, pillReminder.getId());
		MotechEvent event = new MotechEvent(subject, parameters);
		return event;
	}

}
