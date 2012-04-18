package org.motechproject.demo.commcare.listeners;
import java.util.Properties;

import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.demo.commcare.domain.CommcareUser;
import org.motechproject.demo.commcare.services.CommcareUserService;
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
	
	@Autowired
	private CommcareUserService commcareUserService;
	
	@Autowired
	@Qualifier(value="smsMessages")
	private Properties smsMessages;

	@MotechListener(subjects = { EventSubjects.MILESTONE_ALERT })
	public void listenOnFormMilestone(MotechEvent event) {
		System.out.println("Milestone alert!");

		MilestoneEvent mEvent = new MilestoneEvent(event);
		String scheduleName = mEvent.getScheduleName();
		
		if (scheduleName.equals("Schedule name here")) {
			handleAlert(mEvent);
		}
	}
	
	private void handleAlert(MilestoneEvent mEvent) {
		String windowName = mEvent.getWindowName();
		String commcareId = mEvent.getExternalId();
		
		System.out.println(mEvent.getMilestoneAlert().getMilestoneName());
		
		CommcareUser user = commcareUserService.getCommcareUserById(commcareId);
		
		String phoneNum = null;
		
		if (user != null) {
			phoneNum = user.getDefaultPhoneNumber();
		}
		
		if (phoneNum == null) {
			return;
		}
		
		if (windowName.equals("late")) {
			System.out.println("Late alert...");
			sendLateNotification(phoneNum);
		} else if (windowName.equals("due")) {
			System.out.println("Due alert...");
			sendDueNotification(phoneNum);
		}
	}
	
	private void sendLateNotification(String phoneNum) {
		smsService.sendSMS(phoneNum, getLateMessage("lateMessage"));
	}
	
	private void sendDueNotification(String phoneNum) {
		smsService.sendSMS(phoneNum, getDueMessage("dueMessage"));
	}
	
	private String getLateMessage(String messageId) {
		return smsMessages.getProperty(messageId);
	}
	
	private String getDueMessage(String messageId) {
		return smsMessages.getProperty(messageId);
	}
	
}
