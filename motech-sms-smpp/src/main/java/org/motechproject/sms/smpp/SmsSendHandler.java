package org.motechproject.sms.smpp;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.service.SmsService;

import java.util.List;

public class SmsSendHandler implements SmsEventHandler {

	private ManagedSmslibService service;

	public SmsSendHandler(ManagedSmslibService service) {
		this.service = service;
	}

	@Override
	@MotechListener(subjects = SmsService.SEND_SMS)
	public void handle(MotechEvent event) throws Exception {
		List<String> recipients = ((List<String>) event.getParameters().get(SmsService.RECIPIENTS));
		String text = (String) event.getParameters().get(SmsService.MESSAGE);
		service.queueMessage(recipients, text);
	}
}