package org.motechproject.sms.smpp;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventSubject;
import org.motechproject.sms.api.service.SmsServiceImpl;

import java.util.List;

public class SmsSendHandler implements SmsEventHandler {

	private ManagedSmslibService service;

	public SmsSendHandler(ManagedSmslibService service) {
		this.service = service;
	}

	@Override
	@MotechListener(subjects = EventSubject.SEND_SMS)
	public void handle(MotechEvent event) throws Exception {
		List<String> recipients = ((List<String>) event.getParameters().get(SmsServiceImpl.RECIPIENTS));
		String text = (String) event.getParameters().get(SmsServiceImpl.MESSAGE);
		service.queueMessage(recipients, text);
	}
}