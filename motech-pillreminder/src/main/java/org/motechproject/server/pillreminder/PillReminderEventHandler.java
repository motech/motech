/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.server.pillreminder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.ektorp.DocumentNotFoundException;
import org.motechproject.context.Context;
import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.gateway.MotechSchedulerGateway;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.pillreminder.api.EventKeys;
import org.motechproject.pillreminder.api.PillReminderService;
import org.motechproject.pillreminder.api.dao.PillReminderDao;
import org.motechproject.pillreminder.api.model.Medicine;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.motechproject.pillreminder.api.model.Schedule;
import org.motechproject.pillreminder.api.model.Status;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.event.annotations.MotechListenerType;
import org.motechproject.server.event.annotations.MotechParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Handles Pill Reminder events
 * @author yyonkov
 *
 */
public class PillReminderEventHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	enum ProcessType { SCHEDULE, UNSCHEDULE };
	
	private MotechSchedulerGateway schedulerGateway = Context.getInstance().getMotechSchedulerGateway();
	
	@Autowired(required=false)
	private EventRelay eventRelay = EventContext.getInstance().getEventRelay();
	
	@Autowired
	private PillReminderService pillReminderService;
	
	@Autowired
	private PillReminderDao pillReminderDao;
	
	private void processReminder(PillReminder reminder, ProcessType t) {
		MotechEvent event = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT);
		event.getParameters().put(EventKeys.PILLREMINDER_ID_KEY, reminder.getId());
		// (un)schedule all reminders
		//TODO clarify if end date is included in the regimen
		Assert.notNull(reminder, "PillReminder must not be null");
		Assert.notNull(reminder.getStartDate(), "PillReminder startDate must not be null");
		Assert.notNull(reminder.getEndDate(), "PillReminder endDate must not be null");
		for( Schedule s : reminder.getSchedules()) {
			// (un)schedule for each time of the day
			Assert.notNull(s, "Schedule must not be null");
			if(t==ProcessType.SCHEDULE) {
				Assert.notNull(s.getStartCallTime(), "Schedule startCallTime must not be null");
				Assert.notNull(s.getEndCallTime(), "Schedule endCallTime must not be null");
				Assert.notNull(s.getRepeatCount(), "Schedule repeatCount must not be null");
				Assert.notNull(s.getRepeatInterval(), "Schedule repeatInterval must not be null");
				event.getParameters().put(EventKeys.SCHEDULE_JOB_ID_KEY, s.getJobId());

				CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(
						event, 
						String.format("0 %d/%d %d-%d * * ?", // "0 min/rep-min hour_begin-hour_end * * ?"
								DateUtils.getFragmentInMinutes(s.getStartCallTime().getTimeOfDate(reminder.getStartDate()), Calendar.HOUR_OF_DAY),  s.getRepeatInterval()/60,  // minutes / repeat interval in minutes
								DateUtils.getFragmentInHours(s.getStartCallTime().getTimeOfDate(reminder.getStartDate()), Calendar.DAY_OF_YEAR), // start hour
								DateUtils.getFragmentInHours(s.getEndCallTime().getTimeOfDate(reminder.getStartDate()), Calendar.DAY_OF_YEAR)  // end hour    
						), 
						reminder.getStartDate(),  // start day
						reminder.getEndDate()  // end day
				);
				schedulerGateway.scheduleJob(cronSchedulableJob);

//					** RepeatingSchedulableJob is insufficient for scheduling start-end days
//					RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(	
//							event,
//							s.getStartCallTime().getTimeOfDate(reminder.getStartDate()),
//							s.getEndCallTime().getTimeOfDate(reminder.getStartDate()), 
//							s.getRepeatCount(),
//							s.getRepeatInterval() * 1000);
//					schedulerGateway.scheduleRepeatingJob(schedulableJob);

			} else {
				schedulerGateway.unscheduleJob(s.getJobId());
				s.setJobId(null);
			}
		}
	}

	
	/**
	 * Responsible for scheduling and re-scheduling of pill reminders
	 * @param event
	 */
	@MotechListener(subjects={EventKeys.PILLREMINDER_CREATED_SUBJECT, EventKeys.PILLREMINDER_UPDATED_SUBJECT}, type=MotechListenerType.NAMED_PARAMETERS)
	public void schedulePillReminder(@MotechParam(EventKeys.PILLREMINDER_ID_KEY) String pillReminderId) {
		try {
			PillReminder reminder = pillReminderService.getPillReminder(pillReminderId);
			processReminder(reminder,ProcessType.SCHEDULE);
		} catch ( DocumentNotFoundException e) {
			logger.error(String.format("PillReminder for ID: %s not found.", pillReminderId));
		}
	}
	
	/**
	 * Responsible for unscheduling pill reminders
	 * @param event
	 */
	@MotechListener(subjects={EventKeys.PILLREMINDER_DELETED_SUBJECT} , type=MotechListenerType.NAMED_PARAMETERS)
	public void unschedulePillReminder(@MotechParam(EventKeys.PILLREMINDER_ID_KEY) String pillReminderId) {
		try {
			PillReminder reminder = pillReminderService.getPillReminder(pillReminderId);
			processReminder(reminder, ProcessType.UNSCHEDULE);
			pillReminderDao.remove(reminder);
		} catch ( DocumentNotFoundException e) {
			logger.error(String.format("PillReminder for ID: %s not found.", pillReminderId));
		}
	}

	/**
	 * Responsible for deciding if all pills have been reported
	 * @param event
	 */
	@MotechListener(subjects={EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT})
	public void receivePillReminderFromScheduler(MotechEvent event) {
		try {
			PillReminder reminder = pillReminderService.getPillReminder(EventKeys.getReminderID(event));
			Assert.notNull(reminder);
			if( !pillReminderService.isPillReminderCompleted(reminder, new Date()) ) {
				eventRelay.sendEventMessage(new MotechEvent(EventKeys.PILLREMINDER_PUBLISH_EVENT_SUBJECT, event.getParameters()));
			}
		} catch ( DocumentNotFoundException e) {
			logger.error(String.format("PillReminder for ID: %s not found.", EventKeys.getReminderID(event)));
		}
	}
	
	/**
	 * Responsible for reporting pill reminder results
	 * @param event
	 */
	@MotechListener(subjects={EventKeys.PILLREMINDER_RESULT_EVENT_SUBJECT})
	public void receivePillReminderResults(MotechEvent event) {
		String transitionName = EventKeys.getStringValue(event, EventKeys.TREE_TRANSITION_ID);
		String medName = EventKeys.getStringValue(event, EventKeys.TREE_NAME_ID);
		String patientId = EventKeys.getStringValue(event, EventKeys.TREE_PATIENT_ID);
		Assert.notNull(patientId, EventKeys.TREE_PATIENT_ID+" must not be null");
		Assert.notNull(medName, EventKeys.TREE_NAME_ID+" must not be null");
		Assert.notNull(transitionName, EventKeys.TREE_TRANSITION_ID+" must not be null");
		
		List<PillReminder> reminders = pillReminderService.getRemindersWithinWindow(patientId, new Date());
		if(reminders.size()==0) {
			logger.warn("No pill reminders found for reporting results.");
			return;
		}
		
 		//The first one in the comma separated list is the current medicine name.
		medName = medName.split(",")[0];
		
		//TODO implementation for 1 reminder only (update for more) 
		PillReminder reminder = reminders.get(0);
		//if(!transitionName.equalsIgnoreCase(EventKeys.TRANSITION_NOT_YET_TAKEN)) {
			reportResult(reminder,medName,transitionName.equalsIgnoreCase(EventKeys.TRANSITION_TAKEN));
		//}
		
		// if all medicine reminders are completed unschedule the schedule with the window
		if( pillReminderService.isPillReminderCompleted(reminder, new Date()) ) {
			Schedule schedule = reminder.getScheduleWithinWindow(new Date());
			schedulerGateway.unscheduleJob(schedule.getJobId());
			schedule.setJobId(null);
		}
	}


	/**
	 * Reports results
	 * @param pillReminder
	 * @param medName
	 * @param equals
	 */
	private void reportResult(PillReminder pillReminder, String medName, boolean taken) {
		Date now = new Date();
		Schedule schedule = pillReminder.getScheduleWithinWindow(now);
		Assert.notNull(schedule);
		Assert.notNull(pillReminder.getMedicines());
		
		for(Medicine medicine : pillReminder.getMedicines()) {
			if(medicine.getName().equalsIgnoreCase(medName)) {
				boolean foundStatus = false;
				for (Status status : medicine.getStatuses()) {
					if(status.getWindowStartTimeWithDate().equals(schedule.getWindowStart().getTimeOfDate(now))) {
						status.setTaken(taken);
						pillReminderDao.update(pillReminder);
						foundStatus = true;
						return;
					}
				}
				if (!foundStatus) {
					Status status = new Status();
					status.setDate(now);
					status.setTaken(taken);
					status.setWindowStartTime(schedule.getWindowStart());
					medicine.getStatuses().add(status);
					pillReminderDao.update(pillReminder);
				}
			}
		}
	}
}
