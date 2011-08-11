package org.motechproject.server.pillreminder;


import java.util.Date;

import org.joda.time.DateTime;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.util.PillReminderTimeUtils;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class ReminderEventHandler {

    @Autowired
    private OutboundEventGateway outboundEventGateway;

    @Autowired
    private AllPillRegimens allPillRegimens;
    
    @Autowired
    private MotechSchedulerService schedulerService;

    private PillReminderTimeUtils pillReminderTimeUtils;

    public ReminderEventHandler() {
        pillReminderTimeUtils = new PillReminderTimeUtils();
    }

    public ReminderEventHandler(OutboundEventGateway outboundEventGateway, AllPillRegimens allPillRegimens, PillReminderTimeUtils pillRegimenTimeUtils, 
    		MotechSchedulerService schedulerService) {
        this.outboundEventGateway = outboundEventGateway;
        this.allPillRegimens = allPillRegimens;
        this.pillReminderTimeUtils = pillRegimenTimeUtils;
        this.schedulerService = schedulerService;
    }

    @MotechListener(subjects = {EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER})
    public void handleEvent(MotechEvent motechEvent) {
        PillRegimen pillRegimen = getPillRegimen(motechEvent);
        Dosage dosage = getDosage(pillRegimen, motechEvent);

        if (!dosage.isTodaysDosageResponseCaptured()) {
            outboundEventGateway.sendEventMessage(createNewMotechEvent(dosage, pillRegimen, motechEvent, EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT));
            if (isFirstReminder(dosage, pillRegimen))
            	scheduleRepeatReminders(motechEvent, pillRegimen, dosage);
        }
    }

	private void scheduleRepeatReminders(MotechEvent motechEvent, PillRegimen pillRegimen, Dosage dosage) {
		DateTime dosageTime = DateUtil.now().withHourOfDay(dosage.getDosageTime().getHour()).withMinuteOfHour(dosage.getDosageTime().getMinute());
		Date startTime = dosageTime.plusMinutes(pillRegimen.getReminderRepeatIntervalInMinutes()).toDate();
		Date endTime =   dosageTime.plusHours(pillRegimen.getReminderRepeatWindowInHours()).toDate();
		MotechEvent repeatingReminderEvent = createNewMotechEvent(dosage, pillRegimen, motechEvent, EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER);
	
		repeatingReminderEvent.getParameters().put(MotechSchedulerService.JOB_ID_KEY, dosage.getId() + DateUtil.now().getMillis());
		RepeatingSchedulableJob retryRemindersJob = new RepeatingSchedulableJob(repeatingReminderEvent, 
									startTime, endTime, pillRegimen.getReminderRepeatIntervalInMinutes() * 60 * 1000);
		schedulerService.scheduleRepeatingJob(retryRemindersJob);
	
	}

    private boolean isFirstReminder(Dosage dosage, PillRegimen pillRegimen) {
		int numberOfReminders = pillReminderTimeUtils.timesPillRemindersSent(dosage, pillRegimen.getReminderRepeatWindowInHours(), pillRegimen.getReminderRepeatIntervalInMinutes());
		return numberOfReminders == 0;
	}

	private MotechEvent createNewMotechEvent(Dosage dosage, PillRegimen pillRegimen, MotechEvent eventRaisedByScheduler, String subject) {
        MotechEvent motechEvent = new MotechEvent(subject);
        motechEvent.getParameters().putAll(eventRaisedByScheduler.getParameters());
        int pillWindow = pillRegimen.getReminderRepeatWindowInHours();
        int retryInterval = pillRegimen.getReminderRepeatIntervalInMinutes();

        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TIMES_SENT, pillReminderTimeUtils.timesPillRemindersSent(dosage, pillWindow, retryInterval));
        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TOTAL_TIMES_TO_SEND, pillReminderTimeUtils.timesPillRemainderWillBeSent(pillWindow, retryInterval));
        return motechEvent;
    }

    private PillRegimen getPillRegimen(MotechEvent motechEvent) {
        final String pillRegimenExternalId = (String) motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        return allPillRegimens.findByExternalId(pillRegimenExternalId);
    }

    private Dosage getDosage(PillRegimen pillRegimen, MotechEvent motechEvent) {
        final String dosageId = (String) motechEvent.getParameters().get(EventKeys.DOSAGE_ID_KEY);
        return findDosage(dosageId, pillRegimen);
    }

    private Dosage findDosage(final String dosageId, PillRegimen pillRegimen) {
        for(Dosage dosage: pillRegimen.getDosages()) {
              if(dosage.getId().equals(dosageId)) return dosage;
        }
        return null;
    }
}
