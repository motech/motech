package org.motechproject.demo.commcare.listeners;
import java.util.Properties;

import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class FormMilestoneListener {
	
	@Autowired
    private CMSLiteService cmsliteService;
    
    @Autowired
    private SmsService smsService;
	
    @Autowired
	private ScheduleTrackingService scheduleTrackingService;
    
	@Autowired
	private AllEnrollments enrollments;

	@MotechListener(subjects = { EventSubjects.MILESTONE_ALERT })
	public void listenOnFormMilestone(MotechEvent event) {
		System.out.println("Milestone alert!");

		MilestoneEvent mEvent = new MilestoneEvent(event);
		String windowName = mEvent.getWindowName();
		String commcareId = mEvent.getExternalId();
		
		if (windowName.equals("late")) {
			System.out.println("Late alert...");
		} else if (windowName.equals("due")) {
			System.out.println("Due alert...");
		}
		
		
	}
	
	private void sendLateNotification(String phoneNum) {
		//get phone num
		smsService.sendSMS(phoneNum, getLateMessage("123Late"));
	}
	
	private void sendDueNotification(String phoneNum) {
		smsService.sendSMS(phoneNum, getDueMessage("123Due"));
	}
	
	private String getLateMessage(String messageId) {
		return "You're late";
	}
	
	private String getDueMessage(String messageId) {
		return "You're due";
	}
	
}
