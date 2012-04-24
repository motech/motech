package org.motechproject.demo.commcare.listeners;
import java.util.Properties;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.demo.commcare.domain.CommcareUser;
import org.motechproject.demo.commcare.services.CommcareUserService;
import org.motechproject.demo.commcare.web.CommcareController;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FormMilestoneListener {
	
	
	
    @Autowired
    private SmsService smsService;
	
    @Autowired
	private ScheduleTrackingService scheduleTrackingService;
	
	@Autowired
	private CommcareUserService commcareUserService;
	
	@Autowired
	@Qualifier(value="smsMessages")
	private Properties smsMessages;

	@MotechListener(subjects = { EventSubjects.MILESTONE_ALERT })
	public void listenOnFormMilestone(MotechEvent event) {

		MilestoneEvent mEvent = new MilestoneEvent(event);
		String scheduleName = mEvent.getScheduleName();
		
		if (scheduleName.equals(CommcareController.SCHEDULE_NAME)) {
			handleAlert(mEvent);
		}
	}
	
	private void handleAlert(MilestoneEvent mEvent) {
		String windowName = mEvent.getWindowName();
		String commcareId = mEvent.getExternalId();
		
		CommcareUser user = commcareUserService.getCommcareUserById(commcareId);
		
		String phoneNum = null;
		
		if (user != null) {
			phoneNum = user.getDefaultPhoneNumber();
		} else {
			return;
		}
		
		if (phoneNum == null) {
			phoneNum = user.getUserData().get("default_phone_number");
			if (phoneNum == null) {
				return;
			} 
		}
		
		if (windowName.equals("late")) {
			sendLateNotification(phoneNum);
			LocalDate now = DateUtil.today();
			Time time = DateUtil.time(DateUtil.now());
			scheduleTrackingService.fulfillCurrentMilestone(commcareId, mEvent.getScheduleName(), now, time);
		} else if (windowName.equals("due")) {
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
